package bot2.ai.areas;

import bot2.GameSettings;
import bot2.Logger;
import bot2.ai.GameStrategy;
import bot2.ai.HillsListener;
import bot2.map.*;
import bot2.map.areas.CircleArea;
import bot2.map.areas.Quadrant;

import java.util.*;

public class Areas implements UnhideListener, AreaHelper, HillsListener {

    private FieldArea[] areas = new FieldArea[Field.MAX_SIZE*Field.MAX_SIZE];

    private int nextNr = 1;

    private Field field;
    private GameSettings settings;
    private final int halfRadius2;
    private final int halfRadius;
    private ReachableFilter reachableFilter;
    private int[][] areasPreliminary;
    private final int viewRadius;
    private HashSet<FieldArea> toCheck = new HashSet<FieldArea>();
    private Map<Integer, Set<FieldArea>> areaPerX = new HashMap<Integer, Set<FieldArea>>();
    private Map<Integer, Set<FieldArea>> areaPerY = new HashMap<Integer, Set<FieldArea>>();
    private AreaDistanceCalculator calculator = new AreaDistanceCalculator();
    private GameStrategy strategy;

    public Areas(Field field, GameSettings settings, GameStrategy strategy) {
        this.field = field;
        this.settings = settings;
        this.strategy = strategy;
        halfRadius2 = settings.getViewRadius2() / 4;
        halfRadius = settings.getViewRadius() / 2;
        reachableFilter = new ReachableFilter(this.field);
        areasPreliminary = new int[field.getCols()][field.getRows()];
        field.setUnhideListener(this);
        viewRadius = this.settings.getViewRadius();
    }

    public FieldArea getNearestUnknownArea(FieldPoint p) {
        FieldArea nearestArea = null;
        long minLength = -1;
        for (FieldArea area: areas) {
            if (area == null) break;
            if (area.isReached()) continue;
            long length = field.getDistance2(area.getCenter(), p);
            if (length < minLength || minLength == -1) {
                minLength = length;
                nearestArea = area;
            }
        }
        return nearestArea;
    }

    public void beforeUpdate() {
        for (FieldArea area: areas) {
            if (area == null) break;
            area.beforeUpdate();
        }
        toCheck.clear();
    }

    public void afterUpdate() {
        for (FieldArea area: toCheck) {
            checkAreaVisible(area);
        }
        List<FieldArea> hillAreas = new ArrayList<FieldArea>(10);
        for (FieldArea area: areas) {
            if (area == null) break;
            if (field.hasOurHill(area.getCenter())) {
                hillAreas.add(area);
            }
            area.afterUpdate();
        }
        calculator.calculate(areas, hillAreas);
    }

    public void onUnhide(int x, int y) {
        addToCheck(areaPerX.get(x));
        addToCheck(areaPerY.get(y));
    }

    private void addToCheck(Collection<FieldArea> areas) {
        if (areas != null) {
            toCheck.addAll(areas);
        }
    }

    private void checkAreaVisible(FieldArea area) {
        FieldPoint center = area.getCenter();
        for (int x = -viewRadius; x <= viewRadius; x++) {
            FieldPoint point = field.normalize(center.x + x, center.y);
            if (field.getItem(point) == Item.UNKNOWN)
                return;
        }
        for (int y = -viewRadius; y <= viewRadius; y++) {
            FieldPoint point = field.normalize(center.x, center.y + y);
            if (field.getItem(point) == Item.UNKNOWN)
                return;
        }
        onAreaReached(area);
    }


    public void onAreaReached(FieldArea area) {
        if (area.isReached()) return;
        FieldPoint center = area.getCenter();
        Collection<FieldPoint> reachableAreaPoints = reachableFilter.filter(
                new CircleArea(field, settings.getViewRadius2()).getPoints(center),
                center
        );
        markAreaPoints(area, reachableAreaPoints);
        area.setReached();
        areaPerX.get(center.x).remove(area);
        areaPerY.get(center.y).remove(area);
        for (Direction dir: Direction.values()) {
            FieldPoint anotherPoint = getReachablePoint(center, area.getNumber(), dir, reachableAreaPoints);
            if (anotherPoint != null) {// && field.getDistance2(area.getCenter(), anotherPoint) >= halfRadius2) {
                int anotherAreaNr = areasPreliminary[anotherPoint.x][anotherPoint.y];
                if (anotherAreaNr != area.getNumber()) {
                    FieldArea anotherArea = get(anotherAreaNr);
                    if (anotherArea == null) {
                        anotherArea = addNewArea(anotherPoint);
                    }
                    if (!anotherArea.isReached())
                        area.addNearestArea(dir, anotherArea);
                }
            }
        }
    }

    private void markAreaPoints(FieldArea area, Collection<FieldPoint> reachableAreaPoints) {
        FieldPoint center = area.getCenter();
        boolean preliminaryOnly = reachableAreaPoints == null;

        List<FieldPoint> possiblePoints = new ArrayList<FieldPoint>();
        for (int dx = -halfRadius; dx <= halfRadius; dx++) {
            for (int dy = -halfRadius; dy <= halfRadius; dy++) {
                possiblePoints.add(field.normalize(center.x + dx, center.y + dy));
            }
        }
        for (FieldPoint p: possiblePoints) {
            if (preliminaryOnly || reachableAreaPoints.contains(p)) {
                int number = area.getNumber();
                if (areasPreliminary[p.x][p.y] == 0) {
                    areasPreliminary[p.x][p.y] = number;
                }
                if (!preliminaryOnly) {
                    field.assignArea(p, number);
                }

            }
        }
    }

    private FieldArea addNewArea(FieldPoint anotherPoint) {
        int nr = nextNr++;
        FieldArea fieldArea = new FieldArea(nr, anotherPoint, this);
        areas[nr-1] = fieldArea;
        fillPreliminaryArea(fieldArea);
        checkAreaVisible(fieldArea);
        if (!fieldArea.isReached()) {
            addToSet(areaPerX, anotherPoint.x, fieldArea);
            addToSet(areaPerY, anotherPoint.y, fieldArea);
        }
        Logger.log("Add new area at " + anotherPoint + " with nr " + nr + (fieldArea.isReached() ? " already reached" : ""));
        return fieldArea;
    }

    private void addToSet(Map<Integer, Set<FieldArea>> areaSet, Integer key, FieldArea area) {
        Set<FieldArea> set = areaSet.get(key);
        if (set == null) {
            set = new HashSet<FieldArea>();
            areaSet.put(key, set);
        }
        set.add(area);
    }

    private void fillPreliminaryArea(FieldArea fieldArea) {
        markAreaPoints(fieldArea, null);
    }

    private FieldPoint getReachablePoint(FieldPoint from, int area, Direction direction, Collection<FieldPoint> reachableAreaPoints) {
        FieldPoint target = null;
        Quadrant quadrant = new Quadrant(field, new CircleArea(field, settings.getViewRadius2()), from, direction);
        while ((target = quadrant.getNextPoint()) != null) {
            if (reachableAreaPoints.contains(target)) {
                break;
            }
        }
        return target;
    }


    public boolean hasAreas() {
        return nextNr > 1;
    }

    public FieldArea addArea(FieldPoint location) {
        return addNewArea(location);
    }

    public int getCount() {
        return nextNr - 1;
    }

    public FieldArea get(int nr) {
        return nr > 0 ? areas[nr-1] : null;
    }

    public FieldArea get(int x, int y) {
        return get(field.getArea(x, y));
    }

    public void updateArea(int areaNr, Item item) {
        FieldArea area = get(areaNr);
        if (area != null) {
            switch (item) {
                case ANT:
                    area.getStat().addAnt();
                    break;
                case ENEMY_ANT:
                    area.getStat().addEnemy();
                    break;
                case FOOD:
                    area.getStat().addFood();
                    break;
            }
        }
    }

    public FieldArea get(FieldPoint location) {
        return get(location.x, location.y);
    }

    public FieldArea[] getAllAreas() {
        return areas;
    }

    public boolean contains(FieldArea area, FieldPoint point) {
        return field.getArea(point.x, point.y) == area.getNumber();
    }

    public boolean shallRevisit(FieldArea area) {
        return strategy.shallRevisit(area);
    }

    public void onOurHill(FieldPoint point) {
        get(point).setOurHill(true);
    }

    public void onOurHillDestroyed(FieldPoint point) {
        get(point).setOurHill(false);
    }

    public void onEnemyHill(FieldPoint point) {
        get(point).setEnemyHill(true);
    }

    public void onEnemyHillDestroyed(FieldPoint point) {
        get(point).setEnemyHill(false);
    }
}

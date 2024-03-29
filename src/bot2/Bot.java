package bot2;

import bot2.ai.*;
import bot2.ai.Ant;
import bot2.ai.areas.Areas;
import bot2.ai.areas.FieldArea;
import bot2.map.*;
import bot2.map.View;
import bot2.map.areas.CircleArea;

import java.io.IOException;
import java.util.*;

import static bot2.Time.time;

public class Bot extends AbstractSystemInputParser implements MoveHelper, AntLocator {

    private GameSettings settings;
    private Field field;
    private Hills hills;
    private List<FieldPoint> updateable = new ArrayList<FieldPoint>();
    private Orders orders = new Orders();
    private List<Ant> ants = new ArrayList<Ant>();
    private List<Ant> oldAnts = new ArrayList<Ant>();
    private Map<FieldPoint, Ant> antsByPoints = new HashMap<FieldPoint, Ant>();
    private AI ai = new AI();
    private CircleArea visibleArea;
    private ReachableFilter reachableFilter;
    private Areas areas;

    public Bot() throws IOException {
        Logger.init();
    }

    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols,
                      int turns, int viewRadius2, int attackRadius2, int spawnRadius2) {

        settings = new GameSettings(attackRadius2, viewRadius2, turnTime);
        field = new Field(rows, cols);
        hills = new Hills(field, settings);
        visibleArea = new CircleArea(field, settings.getViewRadius2());
        reachableFilter = new ReachableFilter(visibleArea);
        areas = new Areas(field, settings, ai);
        hills.setHillsListener(areas);
        ai.setSettings(settings);
    }

    private List<Integer> turnsToStop = Arrays.asList(
            242
    );

    @Override
    public void beforeUpdate() {
        settings.beforeTurn();
        time.startTurn();
        Logger.log("Turn #" + settings.getTurn());
        if (turnsToStop.contains(settings.getTurn())) {
            turnsToStop = turnsToStop;
        }
        for (FieldPoint fp: updateable) {
            field.setItem(fp, Item.LAND);
        }
        updateable.clear();
        hills.beforeUpdate();
        areas.beforeUpdate();
        oldAnts = new ArrayList<Ant>(ants);
        antsByPoints.clear();
        ants.clear();
    }

    @Override
    protected void updateVisible(int x, int y, Item item) {
        field.setItem(x, y, item);
        if (!item.isPersistent()) {
            updateable.add(FieldPoint.point(x, y));
        }
        if (item == Item.ANT) {
            hills.onAnt(x, y);
            addAnt(x, y);
        }
        areas.updateArea(field.getArea(x, y), item);
    }

    private void addAnt(int x, int y) {
        boolean found = false;
        FieldPoint antPoint = FieldPoint.point(x, y);
        Ant antAtPoint = null;
        for (Ant ant: oldAnts) {
            if (ant.getLocation().equals(antPoint)) {
                antAtPoint = ant;
                oldAnts.remove(ant);
                found = true;
                break;
            }
        }
        if (!found) {
            antAtPoint = new Ant(antPoint, this, new View(
                    visibleArea,
                    field,
                    antPoint,
                    reachableFilter));
        }
        ants.add(antAtPoint);
        antsByPoints.put(antPoint, antAtPoint);
    }

    @Override
    protected void updateVisible(int x, int y, Item item, int side) {
        updateVisible(x, y, item);
    }

    @Override
    protected void updateVisibleHill(int x, int y, int side) {
        hills.onHill(x, y, side);
    }

    @Override
    public void afterUpdate() {
        for (Ant ant: oldAnts) {
            ant.die();
            field.setItem(ant.getLocation(), Item.LAND);
        }
        boolean noAreas = !areas.hasAreas();
        for (Ant ant: ants) {
            ant.beforeTurn();
            if (noAreas) {
                areas.addArea(ant.getLocation());
            }
            ant.getVisibleView().replaceInVisibleArea(Item.UNKNOWN, Item.LAND);
        }
        areas.afterUpdate();
        hills.afterUpdate();
        time.completed("Update");
    }

    @Override
    public Orders doTurn() {

        //actions
        try {
            ai.doTurn(ants, field, areas, hills);
        }
        catch (Exception e) {
            Logger.log("Error occured: " + e);
            Logger.error(e);
        }
        finally {
            time.completed("AI");
            long remainingTime = settings.getRemaining();
            if (remainingTime < settings.getTurnTime()/2) {
                Logger.log("WARNING: remaining time " + remainingTime);
            }
            else {
                Logger.log("Remaining time " + remainingTime);

            }
            if (settings.writeFieldToLog()) {
                Logger.logState(field, settings.getTurn(), areas, ants);
            }
            time.logTime();
        }
        return orders;
    }



    public FieldPoint move(FieldPoint point, Direction direction) {
        FieldPoint destination = field.getPoint(point, direction);
        if (field.getItem(destination).isPassable()) {
            field.moveItem(point, destination);
            updateable.add(destination);
            antsByPoints.put(destination, antsByPoints.remove(point));
            orders.addOrder(point, direction);
            return destination;
        }
        return point;

    }

    public FieldPoint move(FieldPoint oldLocation, FieldPoint target) {
        //TODO: do it better
        for (Direction d: Direction.values()) {
            if (field.getPoint(oldLocation, d).equals(target)) {
                return move(oldLocation, d);
            }
        }
        return oldLocation;
    }

    public boolean canMoveTo(FieldPoint target) {
        return field.getItem(target).isPassable();
    }

    public FieldArea getNextAreaOnWayTo(FieldPoint from, FieldArea targetArea) {
        FieldArea fromArea = areas.get(from);
        if (fromArea == null || fromArea.getNearAreas().contains(targetArea)) {
            return targetArea;
        }
        List<FieldArea> path = areas.findPath(fromArea, targetArea);
        if (path.isEmpty()) {
            Logger.log("WARN: Tried to find way from " + fromArea + " to " + targetArea + ", but failed");
            return null;
        }
        else {
            long minDistance = field.getDistance2(from, path.get(0).getCenter());
            int nr = 0;
            for (int i = 1; i < path.size(); i++) {
                long distance = field.getDistance2(from, path.get(i).getCenter());
                if (distance < minDistance) {
                    minDistance = distance;
                    nr = i;
                }
                else {
                    break;
                }
            }
            return path.get(nr);
        }

    }

    public KickResult kickOurAntAt(FieldPoint point) {
        if (field.getItem(point) == Item.ANT) {
            Ant ant = findAnt(point);
            Logger.log("  " + ant + " is on the way, try to kick it");
            ant.cancelSkipping();
            boolean recursive = ant.update();
            if (recursive)
                return KickResult.RECUSRIVE_KICK;
            return ant.getLocation().equals(point) ? KickResult.NOT_KICKED : KickResult.KICKED_OUT;
        }
        return KickResult.NOT_KICKED;
    }

    private Ant findAnt(FieldPoint point) {
        return antsByPoints.get(point);
    }

    public boolean isItemMoving(FieldPoint point) {
        Ant ant = findAnt(point);
        return ant == null || !ant.isSkippingStep();
    }

    public void goingToWalkTo(FieldPoint nextLocation) {

    }

    public Ant getAnt(FieldPoint point) {
        return findAnt(point);
    }
}

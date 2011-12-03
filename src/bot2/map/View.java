package bot2.map;

import bot2.map.areas.Area;
import pathfinder.PointHelper;

import java.util.*;

public class View {

    private FieldPoint point;
    private Area area;
    private Field field;
    private PointsFilter reachableFilter;
    private boolean empty = true;

    private Map<Item, List<FieldPoint>> reachableObjects = new HashMap<Item, List<FieldPoint>>();
    private Collection<FieldPoint> visibleCoords = null;
    private Collection<FieldPoint> reachableCoords = null;

    public View(Area area, Field field, FieldPoint point, PointsFilter reachableFilter) {
        this.point = point;
        this.field = field;
        this.area = area;
        this.reachableFilter = reachableFilter;
        resetView();
    }

    public void resetView() {
        reachableObjects.clear();
        visibleCoords = area.getPoints(point);
        empty = true;
    }

    public void setPoint(FieldPoint point) {
        this.point = point;
        resetView();
    }

    private void initLists() {
        if (empty) {
            reachableCoords = reachableFilter.filter(visibleCoords, point);
            for (FieldPoint t: reachableCoords ) {
                if (t.equals(point)) continue;//skip self

                Item item = field.getItem(t);
                List<FieldPoint> points = reachableObjects.get(item);
                if (points == null) {
                    points = new ArrayList<FieldPoint>();
                    reachableObjects.put(item, points);
                }
                points.add(t);
            }
            empty = false;
        }
    }

    public List<FieldPoint> getItems(Item item) {
        initLists();
        return reachableObjects.containsKey(item) ? reachableObjects.get(item) : Collections.<FieldPoint>emptyList();
    }

    public int getItemsCount(Item item) {
        initLists();
        return reachableObjects.containsKey(item) ? reachableObjects.get(item).size() : 0;
    }

    public int getFriendsCount() {
        return getItemsCount(Item.ANT);
    }

    public List<FieldPoint> getFriends() {
        return getItems(Item.ANT);
    }

    public List<FieldPoint> getEnemies() {
        return getItems(Item.ENEMY_ANT);
    }

    public int getEnemiesCount() {
        return  getItemsCount(Item.ENEMY_ANT);
    }

    public void replaceInVisibleArea(Item what, Item withWhat) {
        for (FieldPoint point: visibleCoords) {
            field.replace(point, what, withWhat);
        }
    }

    public boolean see(FieldPoint coord) {
        return visibleCoords.contains(coord);
    }

    public boolean canReach(FieldPoint coord) {
        return reachableCoords.contains(coord);
    }


    public void assignAreaInReachableArea(int area) {
        initLists();
        for (FieldPoint point: reachableCoords) {
            field.assignArea(point, area);
        }
    }

    public PointHelper<FieldPoint> producePointHelper() {
        return new VisibleAreaPathHelper(this, field);
    }

    public Item getItem(FieldPoint point) {
        return field.getItem(point);
    }

    public boolean isNear(FieldPoint point) {
        return field.getQuickDistance(point, this.point) <= 1;
    }

}

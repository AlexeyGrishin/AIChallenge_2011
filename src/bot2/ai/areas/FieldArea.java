package bot2.ai.areas;

import bot2.map.Direction;
import bot2.map.FieldPoint;

import java.util.ArrayList;
import java.util.Collection;

public class FieldArea {

    private int number;
    private boolean reached = false;
    private FieldPoint center;
    private FieldArea[] nearAreasByDirection = new FieldArea[4];
    private ArrayList<FieldArea> nearAreas = new ArrayList<FieldArea>(8);
    private FieldAreaStat stat = new FieldAreaStat(this);
    private FieldAreaKind kind = FieldAreaKind.NOT_VISITED;
    private AreaHelper helper;
    private int distanceToHill = -1;
    private boolean isOurHill = false, isEnemyHill = false;

    public FieldArea(int number, FieldPoint center, AreaHelper helper) {
        this.number = number;
        this.center = center;
        this.helper = helper;
    }

    public void beforeUpdate() {
        stat.beforeUpdate();
    }

    public boolean isReached() {
        return reached;
    }

    public FieldPoint getCenter() {
        return center;
    }

    public int getNumber() {
        return number;
    }

    public void setReached() {
        reached = true;
    }

    public int getDistanceToHill() {
        return distanceToHill;
    }

    void setDistanceToHill(int distanceToHill) {
        this.distanceToHill = distanceToHill;
    }

    public void addNearestArea(Direction dir, FieldArea area) {
        doAddArea(dir, area);
        area.doAddArea(dir.opposite(), this);
    }

    private boolean doAddArea(Direction dir, FieldArea area) {
        boolean wasAdded = false;
        if (!nearAreas.contains(area)) {
            nearAreas.add(area);
            wasAdded = true;
        }
        nearAreasByDirection[dir.ordinal()] = area;
        return wasAdded;
    }

    public Collection<? extends FieldArea> getNearAreas() {
        return nearAreas;
    }

    /**
     *
     * @param direction
     * @return latest added area in following direction.
     *
     * Do NOT use this method to iterate over all nearest collections! Use {@link #getNearAreas()} instead
     */
    public FieldArea getNearArea(Direction direction) {
        return nearAreasByDirection[direction.ordinal()];
    }

    public FieldAreaStat getStat() {
        return stat;
    }

    public String toString() {
        return "Area#" + getNumber() + "(" + getCenter() + ")" + kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldArea fieldArea = (FieldArea) o;

        if (center != null ? !center.equals(fieldArea.center) : fieldArea.center != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return center != null ? center.hashCode() : 0;
    }

    public boolean containsPoint(FieldPoint point) {
        return helper.contains(this, point);
    }

    public void afterUpdate() {
        updateKind();
    }

    private void updateKind() {
        if (!isReached()) {
            kind = FieldAreaKind.NOT_VISITED;
        }
        else if (isEnemyHill) {
            kind = FieldAreaKind.ENEMY_HILL;
        }
        else if (helper.shallRevisit(this)) {
            kind = FieldAreaKind.VISITED_TIME_AGO;
        }
        else {
            kind = FieldAreaKind.JUST_VISITED;
        }
    }

    public FieldAreaKind getKind() {
        return kind;
    }

    public void setOurHill(boolean exists) {
        isOurHill = exists;
        updateKind();
    }

    public void setEnemyHill(boolean exists) {
        isEnemyHill = exists;
        updateKind();
    }

    public boolean getIsOurHill() {
        return isOurHill;
    }
}

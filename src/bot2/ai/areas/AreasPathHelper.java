package bot2.ai.areas;

import bot2.map.Field;
import pathfinder.PointHelper;

public class AreasPathHelper implements PointHelper<FieldArea> {

    private Field field;

    public AreasPathHelper(Field field) {
        this.field = field;
    }

    public int getCost(FieldArea coord) {
        switch (coord.getKind()) {
            case JUST_ATTACKED: return 10;
            default: return 1;
        }
    }

    public int getDefaultCost() {
        return 1;
    }

    public boolean isReachable(FieldArea coord) {
        return coord.isReached();
    }

    public Iterable<? extends FieldArea> getNearestCells(FieldArea coord) {
        return coord.getNearAreasCollection();
    }

    public int getQuickDistanceBetween(FieldArea source, FieldArea target) {
        return field.getQuickDistance(source.getCenter(), target.getCenter());
    }

    public double getDistanceBetween(FieldArea source, FieldArea target) {
        return field.getDistance(source.getCenter(), target.getCenter());
    }
}

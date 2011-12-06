package bot2.map;

import pathfinder.PointHelper;

import java.util.Arrays;

public class VisibleAreaPathHelper implements PointHelper<FieldPoint> {

    private View view;
    private Field field;

    public VisibleAreaPathHelper(View view, Field field) {
        this.view = view;
        this.field = field;
    }

    public int getCost(FieldPoint coord) {
        return field.hasOurHill(coord) ? 10 : 1;
    }

    public int getDefaultCost() {
        return 1;
    }

    public boolean isReachable(FieldPoint coord) {
        return field.getItem(coord).isProbablyPassable(view.isNear(coord));
    }

    public Iterable<? extends FieldPoint> getNearestCells(FieldPoint coord) {
        return Arrays.asList(
                field.getPoint(coord, Direction.E),
                field.getPoint(coord, Direction.W),
                field.getPoint(coord, Direction.N),
                field.getPoint(coord, Direction.S)
        );
    }

    public int getQuickDistanceBetween(FieldPoint source, FieldPoint target) {
        return field.getQuickDistance(source, target);
    }

    public double getDistanceBetween(FieldPoint source, FieldPoint target) {
        return field.getDistance(source, target);
    }


}

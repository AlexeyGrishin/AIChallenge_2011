package pathfinder;

public interface PointHelper<C> {
    int getCost(C coord);

    int getDefaultCost();

    boolean isReachable(C coord);

    Iterable<? extends C> getNearestCells(C coord);

    int getQuickDistanceBetween(C source, C target);

    double getDistanceBetween(C source, C target);
}

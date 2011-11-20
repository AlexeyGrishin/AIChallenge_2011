package pathfinder;

import java.util.*;

public class PathFinder<C> {

    private boolean targetIsReachable;
    private int cellsCount = 0;
    private long time = 0;


    public static class PathElement<C> {
        public final C from;
        public final C to;
        public final int fromCost;
        public final int toCost;

        public PathElement(C from, C to, int fromCost, int toCost) {
            this.from = from;
            this.to = to;
            this.fromCost = fromCost;
            this.toCost = toCost;
        }

        public String toString() {
            return from + " -> " + to;
        }
    }

    /**
     * Finds the fastest path from point <c>from</c> to point <c>to</c> if it is reachable.
     * @param helper movement cost helper
     * @param from start point
     * @param to end point
     */

    public PathFinder(PointHelper<C> helper, C from, C to) {
        this.helper = helper;
        source = from;
        target = to;
        targetIsReachable = helper.isReachable(target);
        if (source.equals(target)) {
            RETURN_EMPTY.formNotFoundPath();
        }
    }

    public void findPath() {
        if (found()) return;
        long startTime = System.currentTimeMillis();

        addToOpenedList(source, null);

        while (!openedCells.isEmpty() && !notFoundStrategy.isLimitReached(cellsCount, time)) {
            processNextOpened();
            time = System.currentTimeMillis() - startTime;
            cellsCount = closedCells.size();
        }

        if (!found())
            notFoundStrategy.formNotFoundPath();

    }

     public PathNotFoundStrategy RETURN_EMPTY = new PathNotFoundStrategy() {
        public void formNotFoundPath() {
            foundPath = Collections.emptyList();
        }

         public boolean isLimitReached(int count, long time) {
             return false;
         }
     };

    public PathNotFoundStrategy RETURN_PATH_TO_NEAREST = new PathNotFoundStrategy() {
        public void formNotFoundPath() {
            Cell newFinish = Collections.min(allCells.values(), BY_DISTANCE_TO_TARGET);
            formResult(newFinish);
        }

        public boolean isLimitReached(int count, long time) {
            return false;
        }
    };

    public PathNotFoundStrategy limited(final int countLimit, final PathNotFoundStrategy strategy) {
        return new PathNotFoundStrategy() {
            public void formNotFoundPath() {
                strategy.formNotFoundPath();
            }

            public boolean isLimitReached(int count, long time) {
                return count >= countLimit;
            }
        };
    }

    public void setNotFoundStrategy(PathNotFoundStrategy notFoundStrategy) {
        this.notFoundStrategy = notFoundStrategy;
    }

    private Collection<PathElement<C>> foundPath = null;
    private PointHelper<C> helper;
    private C source;
    private C target;

    private PathNotFoundStrategy notFoundStrategy = RETURN_EMPTY;

    private class Cell {
        private C coord;
        private Cell source = null;
        private int selfCost;
        private int distanceToTarget;
        private int movementCostFromStart;

        private Cell(C coord, Cell source, int selfCost) {
            this.coord = coord;
            this.distanceToTarget = helper.getDefaultCost() * helper.getQuickDistanceBetween(coord, target);
            this.source = source;
            this.selfCost = selfCost;
            updateMovementCostFromStart();
        }

        private void updateSource(Cell sourceCell) {
            if (sourceCell == null) return;
            if (this.source != null && this.source.movementCostFromStart > sourceCell.movementCostFromStart) {
//                System.out.println("Updated cell " + this + ", new source is " + sourceCell);
                this.source = sourceCell;
                updateMovementCostFromStart();
            }
        }

        private void updateMovementCostFromStart() {
            this.movementCostFromStart = this.source != null
                    ? this.source.movementCostFromStart + (int)(selfCost * helper.getDistanceBetween(this.source.coord, coord))
                    : 0;
        }

        int getCost() {
            return distanceToTarget + movementCostFromStart;
        }


        public boolean equals(Object o) {
            return ((Cell)o).coord.equals(coord);
        }

        public int hashCode() {
            return coord.hashCode();
        }

        public String toString() {
            return (source == null ? "" : (source.coord + " -> ")) + coord + "(" + selfCost + ", " + movementCostFromStart + "," + distanceToTarget + ") ";
        }
    }

    private Comparator<Cell> BY_COST = new Comparator<Cell>() {
        public int compare(Cell o1, Cell o2) {
            int res = o1.getCost() - o2.getCost();
            if (res == 0) {
                res = o1.distanceToTarget - o2.distanceToTarget;
            }
            return res;
        }
    };

    private final Comparator<Cell> BY_DISTANCE_TO_TARGET = new Comparator<Cell>() {
        public int compare(Cell o1, Cell o2) {
            int res = o1.distanceToTarget - o2.distanceToTarget;
            if (res == 0) {
                res = o1.movementCostFromStart - o2.movementCostFromStart;
            }
            return res;
        }
    };


    private Set<Cell> openedCells = new HashSet<Cell>();
    private Map<C, Cell> allCells = new HashMap<C, Cell>();
    private Set<C> closedCells = new HashSet<C>();

    private void processNextOpened() {
        Cell currentCell = Collections.min(openedCells, BY_COST);
        //System.out.println("Select " + currentCell + " from " + openedCells);
        openedCells.remove(currentCell);
        closedCells.add(currentCell.coord);
        if (currentCell.coord.equals(target)) {
            openedCells.clear();
            formResult(currentCell);
            return;
        }
        for (C nearestC: helper.getNearestCells(currentCell.coord)) {
            addToOpenedList(nearestC, currentCell);
        }
    }

    private void formResult(Cell currentCell) {
        LinkedList<PathElement<C>> foundPath = new LinkedList<PathElement<C>>();
        Cell source = currentCell.source;
        boolean first = true;
        while (source != null) {
            if (!first || targetIsReachable )
                foundPath.addFirst(new PathElement<C>(source.coord, currentCell.coord, source.selfCost, currentCell.selfCost));
            first = false;
            currentCell = source;
            source = currentCell.source;
        }
        this.foundPath = foundPath;
    }

    private void addToOpenedList(C currentPoint, Cell source) {
        if (!closedCells.contains(currentPoint)) {
            if (currentPoint == this.source || currentPoint == this.target || helper.isReachable(currentPoint)) {
                Cell cell = new Cell(currentPoint, source, helper.getCost(currentPoint));
                openedCells.add(cell);
                allCells.put(currentPoint, cell);
                //System.out.println("Added to opened: " +cell);
            }
        }
        else {
            allCells.get(currentPoint).updateSource(source);

        }
    }


    public Collection<PathElement<C>> getFoundPath() {
        return foundPath;
    }

    public boolean found() {
        return foundPath != null;
    }

    public int getObservedCells() {
        return cellsCount;
    }

    public long getSpentTime() {
        return time;
    }


}

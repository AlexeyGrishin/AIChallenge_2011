package bot2.map;

import bot2.map.areas.Area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class ReachableFilter implements PointsFilter {

    private Area area;

    public ReachableFilter(Area area) {
        this.area = area;
    }

    public Collection<FieldPoint> filter(Collection<FieldPoint> points, FieldPoint areaCenter) {
        return getResult(points, areaCenter).getReachablePoints();
    }

    public ReachableFiltrationResult getResult(Collection<FieldPoint> points, FieldPoint areaCenter, int distanceLimit) {
        ReachableFiltrationResult result = new ReachableFiltrationResult(areaCenter);
        fillReachable(new HashSet<FieldPoint>(points), result, areaCenter, distanceLimit);
        return result;

    }
    public ReachableFiltrationResult getResult(Collection<FieldPoint> points, FieldPoint areaCenter) {
        return getResult(points, areaCenter, Integer.MAX_VALUE);
    }

    private void fillReachable(Collection<FieldPoint> allPoints, ReachableFiltrationResult reachablePoints, FieldPoint startPoint, int distanceLimit) {
        LinkedList<FieldPoint> pointsToProcess = new LinkedList<FieldPoint>();
        LinkedList<Integer> pointDistance = new LinkedList<Integer>();
        HashSet<FieldPoint> pointsToProcessSet = new HashSet<FieldPoint>();
        pointsToProcess.add(startPoint);
        pointDistance.add(0);
        while (!pointsToProcess.isEmpty()) {
            FieldPoint point = pointsToProcess.removeFirst();
            Integer distance = pointDistance.removeFirst();
            allPoints.remove(point);
            reachablePoints.add(point, distance);
            if (distance >= distanceLimit) continue;
            for (Direction direction: Direction.values()) {
                FieldPoint nextPoint = area.getPointAt(point, direction);
                if (allPoints.contains(nextPoint)
                        && !pointsToProcessSet.contains(nextPoint)
                        //&& !reachablePoints.contains(nextPoint)
                        && isReachable(nextPoint)) {
                    pointsToProcess.addLast(nextPoint);
                    pointsToProcessSet.add(nextPoint);
                    pointDistance.add(distance+1);
                }
            }
        }
    }

    protected boolean isReachable(FieldPoint nextPoint) {
        return area.getItem(nextPoint).isWalkable();
    }


}

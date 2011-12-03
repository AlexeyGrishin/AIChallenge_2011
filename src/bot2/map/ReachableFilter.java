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
        HashSet<FieldPoint> reachablePoints = new HashSet<FieldPoint>();
        fillReachable(new ArrayList<FieldPoint>(points), reachablePoints, areaCenter);
        return reachablePoints;
    }

    private void fillReachable(Collection<FieldPoint> allPoints, HashSet<FieldPoint> reachablePoints, FieldPoint startPoint) {
        LinkedList<FieldPoint> pointsToProcess = new LinkedList<FieldPoint>();
        HashSet<FieldPoint> pointsToProcessSet = new HashSet<FieldPoint>();
        pointsToProcess.add(startPoint);
        while (!pointsToProcess.isEmpty()) {
            FieldPoint point = pointsToProcess.removeFirst();
            allPoints.remove(point);
            reachablePoints.add(point);
            for (Direction direction: Direction.values()) {
                FieldPoint nextPoint = area.getPointAt(point, direction);
                if (allPoints.contains(nextPoint)
                        && !pointsToProcessSet.contains(nextPoint)
                        //&& !reachablePoints.contains(nextPoint)
                        && area.getItem(nextPoint).isWalkable()) {
                    pointsToProcess.addLast(nextPoint);
                    pointsToProcessSet.add(nextPoint);
                }
            }
        }



    }


}

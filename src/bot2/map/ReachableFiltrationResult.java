package bot2.map;

import bot2.map.areas.Area;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ReachableFiltrationResult {

    private Collection<FieldPoint> reachablePoints = new HashSet<FieldPoint>();
    private FieldPoint source;

    private Map<FieldPoint, Integer> distances = new HashMap<FieldPoint, Integer>();

    public ReachableFiltrationResult(FieldPoint source) {
        this.source = source;
    }

    public void add(FieldPoint point, int distance) {
        reachablePoints.add(point);
        distances.put(point, distance);
    }

    public int getDistanceTo(FieldPoint point) {
        return distances.get(point);
    }

    public Collection<FieldPoint> getReachablePoints() {
        return reachablePoints;
    }
}

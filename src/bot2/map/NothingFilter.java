package bot2.map;

import java.util.Collection;

public class NothingFilter implements PointsFilter {
    public Collection<FieldPoint> filter(Collection<FieldPoint> points, FieldPoint areaCenter) {
        return points;
    }
}

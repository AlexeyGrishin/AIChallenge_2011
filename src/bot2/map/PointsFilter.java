package bot2.map;

import java.util.Collection;

public interface PointsFilter {

    public Collection<FieldPoint> filter(Collection<FieldPoint> points, FieldPoint areaCenter);
}

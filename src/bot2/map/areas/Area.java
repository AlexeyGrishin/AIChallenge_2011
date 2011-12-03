package bot2.map.areas;

import bot2.map.Direction;
import bot2.map.FieldPoint;
import bot2.map.Item;

import java.util.Collection;

public interface Area {

    public Collection<FieldPoint> getPoints(FieldPoint areaCenter);

    public Item getItem(FieldPoint point);

    public FieldPoint getPointAt(FieldPoint point, Direction direction);

}

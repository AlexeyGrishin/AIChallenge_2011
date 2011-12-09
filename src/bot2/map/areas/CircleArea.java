package bot2.map.areas;

import bot2.map.*;
import pathfinder.MathCache;

import java.util.*;

public class CircleArea implements Area {

    private static AreasCache<Integer> cache = new AreasCache<Integer>(new AreaCalculator<Integer>() {
        public List<ViewPoint> calculate(Integer radius2) {
            List<ViewPoint> points = new ArrayList<ViewPoint>(radius2*4);
            int radius = sqrt(radius2);
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    long distance = MathCache.square(x) + MathCache.square(y);
                    if (distance <= radius2) {
                        points.add(new ViewPoint(x, y));
                    }
                }
            }
            return points;
        }
    });

    private static int sqrt(Integer radius2) {
        return (int)Math.floor(MathCache.sqrt(radius2));
    }

    List<ViewPoint> getPoints() {
        return cache.getPoints(radius2);
    }

    private Field field;
    private int radius2;

    public CircleArea(Field field, int radius2) {
        this.field = field;
        this.radius2 = radius2;
    }

    public Collection<FieldPoint> getPoints(FieldPoint areaCenter) {
        HashSet<FieldPoint> points = new HashSet<FieldPoint>();
        for (ViewPoint p: cache.getPoints(radius2)) {
            FieldPoint t = p.getPoint(field, areaCenter);
            points.add(t);
        }
        return points;
    }

    public Item getItem(FieldPoint point) {
        return field.getItem(point);
    }

    public FieldPoint getPointAt(FieldPoint point, Direction direction) {
        return field.getPoint(point, direction);
    }

    public int getRadius2() {
        return radius2;
    }

    public QuickArea toQuickArea() {
        return new QuickArea(getPoints(), 2*sqrt(radius2));
    }
}

package bot2.map.areas;

import bot2.map.Direction;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.areas.CircleArea;
import pathfinder.MathCache;

import java.util.*;

public class Quadrant implements Area {

    private Field field;
    private FieldPoint center;

    static class QuadrantKey {
        final CircleArea area;   //shall NOT be used in equas/hashCode
        final int radius2;
        final Direction direction;

        QuadrantKey(int radius2, Direction direction, CircleArea area) {
            this.radius2 = radius2;
            this.direction = direction;
            this.area = area;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QuadrantKey that = (QuadrantKey) o;

            if (radius2 != that.radius2) return false;
            if (direction != that.direction) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = radius2;
            result = 31 * result + (direction != null ? direction.hashCode() : 0);
            return result;
        }
    }
    private static AreasCache<QuadrantKey> cache = new AreasCache<QuadrantKey>(new AreaCalculator<QuadrantKey>() {
        public List<ViewPoint> calculate(final QuadrantKey key) {
            List<ViewPoint> points = key.area.getPoints();
            List<ViewPoint> pointsToReturn = new ArrayList<ViewPoint>(points.size() / 4);
            for (ViewPoint point: points) {
                boolean matchDirectionX = (point.dx < 0 && key.direction.dx < 0) || (point.dx > 0 && key.direction.dx > 0);
                boolean matchDirectionY = (point.dy < 0 && key.direction.dy < 0) || (point.dy > 0 && key.direction.dy > 0);
                if (matchDirectionX || matchDirectionY) {
                    boolean matchQuadrant = false;
                    switch (key.direction) {
                        case N:
                        case S:
                            matchQuadrant = Math.abs(point.dy) > Math.abs(point.dx);
                            break;
                        case E:
                        case W:
                            matchQuadrant = Math.abs(point.dx) > Math.abs(point.dy);
                            break;
                    }
                    if (matchQuadrant)
                        pointsToReturn.add(point);
                }
            }
            Collections.sort(pointsToReturn, new Comparator<ViewPoint>() {
                public int compare(ViewPoint o1, ViewPoint o2) {
                    switch (key.direction) {
                        case N:
                            int dy = (o1.dy - o2.dy);
                            return dy != 0 ? dy : Math.abs(o1.dx) - Math.abs(o2.dx);
                        case S:
                            dy = -(o1.dy - o2.dy);
                            return dy != 0 ? dy : Math.abs(o1.dx) - Math.abs(o2.dx);
                        case E:
                            int dx = -(o1.dx - o2.dx);
                            return dx != 0 ? dx : Math.abs(o1.dy) - Math.abs(o2.dy);
                        case W:
                            dx = (o1.dx - o2.dx);
                            return dx != 0 ? dx : Math.abs(o1.dy) - Math.abs(o2.dy);
                    }
                    return 0;
                }
            });

            return pointsToReturn;
        }

    });

    private List<ViewPoint> points = new ArrayList<ViewPoint>();
    private int nr = 0;

    public Quadrant(Field field, CircleArea area, FieldPoint center, Direction direction) {
        this.field = field;
        this.center = center;
        points = cache.getPoints(new QuadrantKey(area.getRadius2(), direction, area));
    }


    public FieldPoint getNextPoint() {
        if (nr >= points.size()) return null;
        ViewPoint point = points.get(nr++);
        return point.getPoint(field, center);
    }

    public Collection<FieldPoint> getPoints(FieldPoint areaCenter) {
        List<FieldPoint> points = new ArrayList<FieldPoint>();
        FieldPoint p;
        while ((p = getNextPoint()) != null) {
            points.add(p);
        }
        return points;
    }

    public Item getItem(FieldPoint point) {
        return field.getItem(point);
    }

    public FieldPoint getPointAt(FieldPoint point, Direction direction) {
        return field.getPointAt(point, direction);
    }
}

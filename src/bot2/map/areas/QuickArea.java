package bot2.map.areas;

import bot2.map.FieldPoint;

import java.util.List;

public class QuickArea {

    private final boolean[][] map;
    private final int size;
    private final int halfSize;

    public QuickArea(List<ViewPoint> viewPoints, int halfSize) {
        this.size = halfSize*2;
        this.halfSize = halfSize;
        map = new boolean[size][size];
        for (ViewPoint point: viewPoints) {
            int x = point.dx + halfSize;
            int y = point.dy + halfSize;
            map[x][y] = true;
        }
    }

    public boolean containsPoth(FieldPoint point1, FieldPoint point2) {
        int x = (point1.x - point2.x) + halfSize;
        if (x < 0 || x >= size) return false;
        int y = (point1.y - point2.y) + halfSize;
        if (y < 0 || y >= size) return false;
        return map[x][y];
    }
}

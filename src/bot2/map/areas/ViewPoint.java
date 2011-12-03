package bot2.map.areas;

import bot2.map.Field;
import bot2.map.FieldPoint;

class ViewPoint {
    int dx, dy;

    ViewPoint(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    FieldPoint getPoint(Field field, FieldPoint point) {
        return field.normalize(point.x + dx, point.y + dy);
    }

    public String toString() {
        return this.dx + "," + this.dy;
    }
}

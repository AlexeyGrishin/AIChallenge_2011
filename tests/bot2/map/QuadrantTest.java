package bot2.map;

import bot2.map.areas.CircleArea;
import bot2.map.areas.Quadrant;
import org.junit.Test;
import util.MockField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QuadrantTest {

    @Test
    public void testDirNRadius8() {
        MockField field = new MockField(20, 20);
        Quadrant quadrant = new Quadrant(
                field, new CircleArea(field, 64),
                FieldPoint.point(10, 10), Direction.N
        );
        assertEquals(FieldPoint.point(10, 2), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(10, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(9, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(11, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(8, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(12, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(7, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(13, 3), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(10, 4), quadrant.getNextPoint());
        skipPoints(quadrant, 10);
        assertEquals(FieldPoint.point(10, 5), quadrant.getNextPoint());
        skipPoints(quadrant, 8);
        assertEquals(FieldPoint.point(10, 6), quadrant.getNextPoint());
        skipPoints(quadrant, 6);
        assertEquals(FieldPoint.point(10, 7), quadrant.getNextPoint());
        skipPoints(quadrant, 4);
        assertEquals(FieldPoint.point(10, 8), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(9, 8), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(11, 8), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(10, 9), quadrant.getNextPoint());
        assertNull(quadrant.getNextPoint());
    }

    @Test
    public void testDirSRadius8() {
        MockField field = new MockField(20, 20);
        Quadrant quadrant = new Quadrant(
                field, new CircleArea(field, 64),
                FieldPoint.point(10, 10), Direction.S
        );
        assertEquals(FieldPoint.point(10, 18), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(10, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(9, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(11, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(8, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(12, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(7, 17), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(13, 17), quadrant.getNextPoint());
    }

    @Test
    public void testDirERadius8() {
        MockField field = new MockField(20, 20);
        Quadrant quadrant = new Quadrant(
                field, new CircleArea(field, 64),
                FieldPoint.point(10, 10), Direction.E
        );
        assertEquals(FieldPoint.point(18, 10), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 10), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 9), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 11), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 8), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 12), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 7), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(17, 13), quadrant.getNextPoint());
        skipPoints(quadrant, 35);
        assertEquals(FieldPoint.point(11, 10), quadrant.getNextPoint());
    }

    @Test
    public void testDirWRadius8() {
        MockField field = new MockField(20, 20);
        Quadrant quadrant = new Quadrant(
                field, new CircleArea(field, 64),
                FieldPoint.point(10, 10), Direction.W
        );
        assertEquals(FieldPoint.point(2, 10), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 10), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 9), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 11), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 8), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 12), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 7), quadrant.getNextPoint());
        assertEquals(FieldPoint.point(3, 13), quadrant.getNextPoint());
        skipPoints(quadrant, 35);
        assertEquals(FieldPoint.point(9, 10), quadrant.getNextPoint());
    }

    private void skipPoints(Quadrant quadrant, int count) {
        for (int i = 0; i < count; i++) quadrant.getNextPoint();
    }

}

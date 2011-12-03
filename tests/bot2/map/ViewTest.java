package bot2.map;

import bot2.map.areas.CircleArea;
import org.junit.Test;
import util.MockField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ViewTest {

    @Test
    public void shallDetectEnemiesInRadiusOnly() throws IOException {
        MockField ants = createMapOfEnemiesWithMyAntInCenter();
        View view = new View(new CircleArea(ants, 4), ants, FieldPoint.point(5, 5), new NothingFilter());
        assertPointsEqual(Arrays.asList(
                new FieldPoint(3, 5),
                new FieldPoint(4, 4), new FieldPoint(4, 5), new FieldPoint(4, 6),
                new FieldPoint(5, 3), new FieldPoint(5, 4), new FieldPoint(5, 6), new FieldPoint(5, 7),
                new FieldPoint(6, 4), new FieldPoint(6, 5), new FieldPoint(6, 6),
                new FieldPoint(7, 5)
        ), view.getEnemies());
        assertEquals(0, view.getFriendsCount());

        ants.logMap();
    }

    private void assertPointsEqual(List<FieldPoint> pointsExpected, List<FieldPoint> pointsActual) {
        List<FieldPoint> actualCopy = new ArrayList<FieldPoint>(pointsActual);
        for (FieldPoint point: pointsExpected) {
            if (pointsActual.contains(point)) {
                actualCopy.remove(point);
            }
            else {
                fail("The point " + point + " is expected, but it is not contained in actual points list: " + pointsActual);
            }
        }
        if (!actualCopy.isEmpty()) {
            fail("The following points are unexpected: " + actualCopy);
        }
    }

    @Test
    public void shallDetectEnemiesAndFriendsInRadiusOnly() throws IOException {
        MockField ants = createMapOfEnemiesWithMyAntInCenter();
        View view = new View(new CircleArea(ants, 4), ants, new FieldPoint(6,6), new NothingFilter());
        assertEquals(11, view.getEnemiesCount());
        assertEquals(1, view.getFriendsCount());

        ants.logMap();
    }

    @Test
    public void shallDetectFriendsInRadiusOnly() throws IOException {
        MockField ants = createMapOfFriendsWithMyAntInCenter();
        View view = new View(new CircleArea(ants, 3), ants, new FieldPoint(5,5), new NothingFilter());
        assertPointsEqual(Arrays.asList(
                new FieldPoint(4, 4), new FieldPoint(5, 4), new FieldPoint(6, 4),
                new FieldPoint(4, 5), new FieldPoint(6, 5),
                new FieldPoint(4, 6), new FieldPoint(5, 6), new FieldPoint(6, 6)
        ), view.getFriends());
        assertEquals(0, view.getEnemiesCount());

        ants.logMap();
    }

    private MockField createMapOfFriendsWithMyAntInCenter() throws IOException {
        return createMapWithMyAntInCenter(Item.ANT);
    }

    private MockField createMapOfEnemiesWithMyAntInCenter() throws IOException {
        return createMapWithMyAntInCenter(Item.ENEMY_ANT);
    }

    private MockField createMapWithMyAntInCenter(Item filler) throws IOException {
        MockField ants = new MockField(10, 10);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                boolean isCenter = col == 5 && row == 5;
                ants.setItem(col, row, isCenter ? Item.ANT : filler);
            }
        }
        return ants;

    }
}
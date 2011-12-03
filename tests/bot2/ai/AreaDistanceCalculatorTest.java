package bot2.ai;

import bot2.ai.areas.AreaDistanceCalculator;
import bot2.ai.areas.AreaHelper;
import bot2.ai.areas.FieldArea;
import bot2.map.Direction;
import bot2.map.FieldPoint;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AreaDistanceCalculatorTest {

    @Test
    public void singleHill_only() {
        AreaDistanceCalculator calculator = new AreaDistanceCalculator();
        FieldArea area0 = createField(1);
        calculator.calculate(new FieldArea[]{area0}, Arrays.asList(area0));
        assertEquals(0, area0.getDistanceToHill());
    }

    @Test
    public void singleHill_nearestArea() {
        AreaDistanceCalculator calculator = new AreaDistanceCalculator();
        FieldArea area0 = createField(1);
        FieldArea area1 = createField(2, area0, Direction.N);
        calculator.calculate(new FieldArea[]{area0, area1}, Arrays.asList(area0));
        assertEquals(0, area0.getDistanceToHill());
        assertEquals(1, area1.getDistanceToHill());

    }

    @Test
    public void singleHill_twoLevelsPartially() {
        AreaDistanceCalculator calculator = new AreaDistanceCalculator();
        FieldArea area0 = createField(1);
        FieldArea area1 = createField(2, area0, Direction.N);
        FieldArea area2 = createField(3, area0, Direction.W);
        FieldArea area3 = createField(4, area0, Direction.E);
        FieldArea area4 = createField(5, area0, Direction.S);
        FieldArea area5 = createField(6, area1, Direction.N);
        FieldArea area6 = createField(7, area1, Direction.W);
        calculator.calculate(new FieldArea[]{area0, area1, area2, area3, area4, area5, area6}, Arrays.asList(area0));
        assertEquals(0, area0.getDistanceToHill());
        assertEquals(1, area1.getDistanceToHill());
        assertEquals(1, area2.getDistanceToHill());
        assertEquals(1, area3.getDistanceToHill());
        assertEquals(1, area4.getDistanceToHill());
        assertEquals(2, area5.getDistanceToHill());
        assertEquals(2, area6.getDistanceToHill());
    }

    @Test
    public void multipleHills_singleLine() {
        AreaDistanceCalculator calculator = new AreaDistanceCalculator();
        FieldArea hill1 = createField(1);
        FieldArea hill2 = createField(2);
        FieldArea npoint1 = createField(3, hill1, Direction.N);
        FieldArea npoint2 = createField(4, hill2, Direction.S);
        FieldArea npoint12 = createField(5, npoint1, Direction.N);
        FieldArea npoint22 = createField(6, npoint2, Direction.S);
        FieldArea npoint3 = createField(7, npoint12, Direction.N);
        npoint22.addNearestArea(Direction.S, npoint3);
        calculator.calculate(new FieldArea[]{npoint3, npoint1, npoint12, npoint22, npoint2, hill1, hill2}, Arrays.asList(hill1, hill2));
        assertEquals(0, hill1.getDistanceToHill());
        assertEquals(0, hill2.getDistanceToHill());
        assertEquals(1, npoint1.getDistanceToHill());
        assertEquals(1, npoint2.getDistanceToHill());
        assertEquals(2, npoint12.getDistanceToHill());
        assertEquals(2, npoint22.getDistanceToHill());
        assertEquals(3, npoint3.getDistanceToHill());
    }


    private FieldArea createField(int nr, FieldArea from, Direction direction) {
        FieldArea area = createField(nr);
        from.addNearestArea(direction, area);
        return area;
    }


    private FieldArea createField(int nr) {
        return new FieldArea(nr, FieldPoint.point(0,2), mock(AreaHelper.class));
    }
}

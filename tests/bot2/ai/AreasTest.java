package bot2.ai;

import bot2.ai.areas.FieldArea;
import bot2.map.Direction;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AreasTest extends AreasTestAbstract {

    @Before
    public void setUp() {
        super.setUp();
        when(settings.getViewRadius()).thenReturn(4);
        when(settings.getViewRadius2()).thenReturn(16);
    }

    @Test
    public void singleAreaMap() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 0);
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));
    }

    @Test
    public void severalAreaMap() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 1);
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));
    }


    @Test
    public void partiallyUnreachable() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 2);
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));
    }

    @Test
    public void complexAreas1() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 3);
        field.logAreasMap();
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));

    }

    @Test
    public void nearestAreas() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 5);
        processAllAreas();
        field.logAreasMap();
        FieldArea area1 = areas.get(0, 7);
        FieldArea area2 = areas.get(0, 3);
        FieldArea area3 = areas.get(3, 7);
        FieldArea area4 = areas.get(3, 3);
        assertNotSame(area1, area2);
        assertNotSame(area1, area3);
        assertNotSame(area1, area4);
        assertNotSame(area2, area3);
        assertNotSame(area2, area4);
        assertNotSame(area3, area4);

        assertSame(area2, area1.getNearArea(Direction.N));
        assertSame(area2, area1.getNearArea(Direction.S));
        assertSame(area3, area1.getNearArea(Direction.E));
        assertSame(area3, area1.getNearArea(Direction.W));

        assertSame(area1, area2.getNearArea(Direction.N));
        assertSame(area1, area2.getNearArea(Direction.S));
        assertSame(area1, area3.getNearArea(Direction.W));
        assertSame(area1, area3.getNearArea(Direction.E));

        assertSame(area4, area3.getNearArea(Direction.N));
        assertSame(area4, area3.getNearArea(Direction.S));
        assertSame(area4, area2.getNearArea(Direction.E));
        assertSame(area4, area2.getNearArea(Direction.W));

        assertSame(area3, area4.getNearArea(Direction.N));
        assertSame(area3, area4.getNearArea(Direction.S));
        assertSame(area2, area4.getNearArea(Direction.E));
        assertSame(area2, area4.getNearArea(Direction.W));

    }

}

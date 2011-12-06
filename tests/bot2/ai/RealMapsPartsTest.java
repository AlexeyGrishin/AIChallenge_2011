package bot2.ai;

import bot2.ai.areas.FieldArea;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.when;

public class RealMapsPartsTest extends AreasTestAbstract {

    @Override
    public void setUp() throws IOException {
        super.setUp();
        when(settings.getViewRadius2()).thenReturn(55);
        when(settings.getViewRadius()).thenReturn(7);
    }

    @Test
    public void maze2_2Areas() throws IOException {
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 4);
        processAllAreas();
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));
    }


    @Test
    public void farArea() throws IOException {
        when(settings.getViewRadius2()).thenReturn(77);
        when(settings.getViewRadius()).thenReturn(8);
        FieldArea cntr = createMapWithArea("tests/bot2/ai/areas.txt", 6);
        assertAreas(cntr.getNearAreas(), field.getMarkedPoints('?'));

    }
}

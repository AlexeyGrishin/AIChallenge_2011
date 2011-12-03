package bot2.ai;

import bot2.ai.areas.FieldArea;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.when;

public class RealMapsPartsTest extends AreasTestAbstract {

    @Override
    public void setUp() {
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
}

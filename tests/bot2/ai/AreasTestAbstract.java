package bot2.ai;

import bot2.GameSettings;
import bot2.ai.areas.Areas;
import bot2.ai.areas.FieldArea;
import bot2.map.FieldPoint;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.MockField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AreasTestAbstract {
    MockField field;
    @Mock GameSettings settings;
    @Mock GameStrategy strategy;
    Areas areas;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(strategy.shallRevisit(any(FieldArea.class))).thenReturn(true);
    }

    protected void assertAreas(Iterable<? extends FieldArea> nearAreas, List<FieldPoint> markedPoints) {
        List<FieldPoint> points = new ArrayList<FieldPoint>(markedPoints);
        field.logAreasMap();
        for (FieldArea area: nearAreas) {
            if (area == null) continue;
            if (!points.remove(area.getCenter())) {
                field.logPoints(Arrays.asList(area.getCenter()));
                fail("Unexpected area: " + area.getCenter() + ", expected one of the following: " + markedPoints);
            }
        }
        if (!points.isEmpty()) {
            fail("Following areas were expected but not found: " + points + " following found: " + nearAreas);
        }
    }

    protected FieldArea createMapWithArea(String file, int nr) {
        try {
            field = new MockField(file, nr);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        areas = new Areas(field, settings, strategy);
        field.makeAllVisible();
        FieldArea firstArea = null;
        for (FieldPoint markedPoint: field.getMarkedPoints('+')) {
            FieldArea area = areas.addArea(markedPoint);
            areas.onAreaReached(area);
            if (firstArea == null)
                firstArea = area;
        }
        return firstArea;
    }

    protected boolean processNextUnknownArea() {
        for (FieldArea area: areas.getAllAreas()) {
            if (area != null && !area.isReached()) {
                areas.onAreaReached(area);
                return true;
            }
        }
        return false;
    }

    protected void processAllAreas() {
        while (processNextUnknownArea());
    }
}

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
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(strategy.shallRevisit(any(FieldArea.class))).thenReturn(true);
    }

    protected void assertAreas(FieldArea[] nearAreas, List<FieldPoint> markedPoints) {
        List<FieldPoint> points = new ArrayList<FieldPoint>(markedPoints);
        field.logAreasMap();
        for (FieldArea area: nearAreas) {
            if (area == null) continue;
            if (!points.remove(area.getCenter())) {
                fail("Unexpected area: " + area.getCenter() + ", expected one of the following: " + markedPoints);
            }
        }
        if (!points.isEmpty()) {
            fail("Following areas were expected but not found: " + points + " following found: " + Arrays.toString(nearAreas));
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
        FieldPoint markedPoint = field.getMarkedPoint('+');
        if (markedPoint != null) {
            FieldArea area = areas.addArea(markedPoint);
            areas.onAreaReached(area);
            return area;
        }
        return null;
    }

    protected boolean processNextUnknownArea() {
        FieldArea area = areas.getNearestUnknownArea(FieldPoint.point(0, 0));
        if (area != null) {
            areas.onAreaReached(area);
            return true;
        }
        return false;
    }

    protected void processAllAreas() {
        while (processNextUnknownArea());
    }
}

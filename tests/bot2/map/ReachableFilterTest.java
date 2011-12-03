package bot2.map;

import bot2.map.areas.Area;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ReachableFilterTest {

    @Mock
    Area area;
    ReachableFilter rArea;
    FieldPoint center;
    List<FieldPoint> points;
    final int MAX = 250;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rArea = new ReachableFilter(area);
    }

    @Test
    public void noWater() {
        initMap(new String[]{
                "        ",
                "        ",
                "        ",
                "        ",
                "        ",
                "        ",
                "        ",
                "        "
        });

        assertEquals(64, doFilter().size());
    }

    private Collection<FieldPoint> doFilter() {
        return rArea.filter(points, center);
    }

    @Test
    public void waterButAllReachable() {
        initMap(new String[]{
                "        ",
                " #####  ",
                "  # ##  ",
                "  #  #  ",
                "     #  ",
                "  ####  ",
                "  # #   ",
                "        "
        });

        assertEquals(64 - 17, doFilter().size());
    }

    @Test
    public void halfUnreachable() {
        initMap(new String[]{
                "        ",
                "        ",
                "########",
                "  #     ",
                "  #     ",
                "  #     ",
                "  #     ",
                "  #     "
        });

        assertEquals(25, doFilter().size());
        assertUnreachable(0, 0);
        assertUnreachable(1, 7);
        assertUnreachable(7, 1);
        assertReachable(3, 3);
    }

    @Test
    public void partUnreachable() {
        initMap(new String[]{
                " #      ",
                "#       ",
                "        ",
                "        ",
                "        ",
                "        ",
                "        ",
                "        "
        });
        assertEquals(61, doFilter().size());
        assertUnreachable(0, 0);
        assertReachable(1, 1);
    }

    private void assertReachable(int x, int y) {
        assertTrue("Expected that point at " + x + "," + y + " is reachable, but it does not",
                doFilter().contains(new FieldPoint(x, y)));
    }

    private void assertUnreachable(int x, int y) {
        assertTrue("Expected that point at " + x + "," + y + " is unreachable, but it does",
                !doFilter().contains(new FieldPoint(x, y)));
    }

    private void initMap(String[] map) {
        points = new ArrayList<FieldPoint>();
        int y = 0;
        for (String str: map) {
            for (int x = 0; x <str.length(); x++) {
                char c = str.charAt(x);
                FieldPoint point = new FieldPoint(x, y);
                when(area.getItem(point)).thenReturn(c == '#' ? Item.WATER : Item.LAND);
                points.add(point);
            }
            y++;
        }
        final int cols = map[0].length();
        final int rows = map.length;
        assertTrue("Test map shall be smaller than MAX", cols < MAX && rows < MAX);
        center = new FieldPoint(cols / 2, rows / 2);
        when(area.getPointAt(any(FieldPoint.class), any(Direction.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                return (((FieldPoint) arguments[0])).getPointAt(
                        ((Direction) arguments[1]), MAX, MAX
                );
            }
        });
    }

}

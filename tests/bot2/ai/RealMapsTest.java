package bot2.ai;

import bot2.GameSettings;
import bot2.Logger;
import bot2.ai.areas.FieldArea;
import bot2.map.FieldPoint;
import bot2.map.ReachableFilter;
import bot2.map.areas.CircleArea;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class RealMapsTest extends AreasTestAbstract {

    private File mapFile;
    private int nr;
    static int NR = 1;

    public RealMapsTest(File mapFile) {
        this.mapFile = mapFile;
        nr = NR++;
    }

    @BeforeClass
    public static void up() throws IOException {
        Logger.init();
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
        setupRanges(settings);
    }

    protected void setupRanges(GameSettings settings) {
        when(settings.getViewRadius()).thenReturn(7);
        when(settings.getViewRadius2()).thenReturn(55);
    }

    @Test
    public void testMap() throws IOException {
        FieldArea area = createMapWithArea(mapFile.getAbsolutePath(), 0);
        if (area == null) {
            System.out.println(mapFile + " skipped");
        }
        else {
            System.out.println(mapFile);
            processAllAreas();
            field.logAreasMap();
            field.checkAllCovered(.05, mapFile.getName(), areas);
            field.checkAreasAmount(areas.getCount(), settings.getViewRadius(), 0.3, mapFile.getName());
            checkAreasLinkedCorrectly();
            Logger.logState(field, mapFile.getName(), areas, Collections.<Ant>emptyList());
        }
    }

    private void checkAreasLinkedCorrectly() {
        List<String> problems = new ArrayList<String>();
        for (FieldArea area: areas.getAllAreas()) {
            if (area == null) break;
            for (FieldArea narea: area.getNearAreas()) {
                CircleArea circleArea = new CircleArea(field, settings.getViewRadius2());

                Collection<FieldPoint> points = new HashSet<FieldPoint>();
                points.addAll(circleArea.getPoints(area.getCenter()));
                points.addAll(circleArea.getPoints(narea.getCenter()));
                if (!new ReachableFilter(field).filter(points, area.getCenter()).contains(narea.getCenter())) {
                    problems.add("Area " + area + " is linked with " + narea + ", but it is impossible to find a way between\n");
                }
            }
        }
        if (!problems.isEmpty()) {
            fail(mapFile + ": " + problems.toString());
        }
    }


    public static class SingleMapTest {
        @Test
        public void test1() throws IOException {
            //RealMapsTest rmt = new RealMapsTest(new File("tools\\maps\\maze\\maze_2.map"));
            RealMapsTest rmt = new RealMapsTest(new File("tools\\maps\\multi_hill_maze\\multi_maze_07.map"));
            RealMapsTest.up();
            rmt.setUp();
            rmt.testMap();
            Logger.logState(rmt.field, rmt.mapFile.getName(), rmt.areas, Collections.<Ant>emptyList());
        }

    }

    @Parameterized.Parameters
    public static Collection<Object[]> mapFiles() {
        List<Object[]> files = new ArrayList<Object[]>();
        File root = new File("tools/maps");
        addMapsFrom(root, files);
        return files;
    }

    private static void addMapsFrom(File dir, List<Object[]> files) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory()) {
                addMapsFrom(file, files);
            }
            if (file.getName().endsWith(".map")) {
                files.add(new Object[] { file });
            }
        }
    }

    public String toString() {
        return mapFile.getName();
    }
}

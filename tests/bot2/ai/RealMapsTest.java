package bot2.ai;

import bot2.ai.areas.FieldArea;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(value = Parameterized.class)
public class RealMapsTest extends AreasTestAbstract {

    private File mapFile;

    public RealMapsTest(File mapFile) {
        this.mapFile = mapFile;
    }

    @Override
    public void setUp() {
        super.setUp();
        when(settings.getViewRadius()).thenReturn(7);
        when(settings.getViewRadius2()).thenReturn(55);
    }

    @Test
    public void testMap() {
        FieldArea area = createMapWithArea(mapFile.getAbsolutePath(), 0);
        if (area == null) {
            System.out.println(mapFile + " skipped");
        }
        else {
            System.out.println(mapFile);
            processAllAreas();
            field.logAreasMap();
            field.checkAllCovered(.97);
            field.checkAreasAmount(areas.getCount(), settings.getViewRadius(), 0.3);
        }
    }


    public static class SingleMapTest {
        @Test
        public void test1() {
            RealMapsTest rmt = new RealMapsTest(new File("tools\\maps\\real\\cell_maze_p02_01.map"));
            rmt.setUp();
            rmt.testMap();
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

package bot2.ai.areas;

import java.util.*;

public class AreaDistanceCalculator {

    public void calculate(FieldArea[] areas, Collection<FieldArea> roots) {
        for (FieldArea area: areas) {
            if (area == null) break;
            area.setDistanceToHill(-1);
        }
        int level = 0;
        List<FieldArea> levelAreas = new LinkedList<FieldArea>(roots);
        processLevel(level, levelAreas);
    }

    private void processLevel(int level, List<FieldArea> levelAreas) {
        List<FieldArea> nextLevelAreas = new LinkedList<FieldArea>();
        for (FieldArea area: levelAreas) {
            area.setDistanceToHill(level);
            for (FieldArea narea: area.getNearAreas()) {
                if (narea.getDistanceToHill() == -1 || narea.getDistanceToHill() > level+1)
                    nextLevelAreas.add(narea);
            }
        }
        if (!nextLevelAreas.isEmpty())
            processLevel(level+1, nextLevelAreas);
    }

}

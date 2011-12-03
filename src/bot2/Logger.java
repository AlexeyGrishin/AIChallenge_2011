package bot2;

import bot2.ai.Ant;
import bot2.ai.areas.Areas;
import bot2.ai.areas.FieldArea;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Logger {

    private static FileWriter fieldWrapper = null;
    private static FileWriter logger = null;

    public static void init() throws IOException {
        fieldWrapper = new FileWriter(new File("q:/programming/java2/projects/aibot/field.js"));
        fieldWrapper.write("var Steps = [];\n");
        logger = new FileWriter(new File("q:/programming/java2/projects/aibot/log.txt"));
    }

    public static void log(String str) {

        try {
            if (logger != null) {
                logger.write(str);
                logger.write("\n");
                logger.flush();
            }
        }
        catch (IOException e) {
            //ignore
        }
    }

    public static void logState(Field field, int turn, Areas areas, List<Ant> ants) {
        if (fieldLogEnabled()) {
            logField(field, turn);
            logAreas(areas);
            logAnts(ants);
        }
    }

    private static void logAnts(List<Ant> ants) {
        StringBuilder targets = new StringBuilder("var ants = []; Steps[Steps.length-1].ants = ants;");
        for (Ant ant: ants) {
            targets.append("ants.push({");
            targets.append("x: ").append(ant.getLocation().x).append(", ");
            targets.append("y: ").append(ant.getLocation().y).append(", ");
            if (!ant.getTargetPoint().equals(ant.getLocation())) {
                targets.append("target: \"").append(ant.getTargetName()).append("\", ");
                targets.append("tx: ").append(ant.getTargetPoint().x).append(", ");
                targets.append("ty: ").append(ant.getTargetPoint().y).append(", ");
            }
            targets.append("a:0");
            targets.append("});\n");
        }
        fieldLog(targets.toString());
    }

    private static void logField(Field field, int turn) {
        String map = "Steps.push({turn: " + turn + ", cols: " + field.getCols() + ", rows: " + field.getRows() + ", field: [";
        StringBuilder bld = new StringBuilder();
        for (int y = 0; y < field.getRows(); y++) {
            bld.append("\"");
            for (int x = 0; x < field.getCols(); x++) {
                //first char - item, second - area
                FieldPoint point = FieldPoint.point(x, y);
                char item = field.getItem(point).getChar();
                char hill = field.hasOurHill(point) ? 'A' : (field.hasEnemyHill(point) ? 'B' : ' ');
                if (item == Item.LAND.getChar()) {
                    item = hill;
                }
                int area = field.getArea(x, y);
                char areaChar = area == 0 ? ' ' : ((char)('a' + (area % 20)));
                bld.append(item).append(areaChar);
            }
            bld.append("\"");
            if (y != field.getRows() - 1) {
                bld.append(",");
            }
            bld.append("\n");
        }
        map += bld + "]});";
        fieldLog(map);
    }


    private static boolean fieldLogEnabled() {
        return fieldWrapper != null;
    }

    private static void fieldLog(String map) {
        try {
            fieldWrapper.write(map);
            fieldWrapper.write("\n");
            fieldWrapper.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void logAreas(Areas areas) {
        StringBuilder areasStr = new StringBuilder("Steps[Steps.length-1].areas = {");
        boolean first = true;
        for (FieldArea area: areas.getAllAreas()) {
            if (area == null) break;
            if (!first) {
                areasStr.append(",");
            }
            areasStr.append("\"").append(area.getNumber()).append("\": {x: ").append(area.getCenter().x).append(", y: ").append(area.getCenter().y).append(", nearest: [");
            boolean firstN = true;
            for (FieldArea n: area.getNearAreasCollection()) {
                areasStr.append(firstN ? "" : ",").append(n.getNumber());
                firstN = false;
            }
            areasStr.append("], ");
            areasStr.append("nr: ").append(area.getNumber()).append(", ");
            areasStr.append("kind: '").append(area.getKind()).append("', ");
            areasStr.append("visitedAgo: ").append(area.getStat().getVisitedTurnsAgo()).append(", ");
            areasStr.append("distance: ").append(area.getDistanceToHill()).append(", ");
            areasStr.append("enemies: ").append(area.getStat().getEnemies()).append(", ");
            areasStr.append("alies: ").append(area.getStat().getAlies()).append(", ");
            areasStr.append("food: ").append(area.getStat().getFoodGatheredTotal()).append(", ");
            areasStr.append("reached: ").append(area.isReached()).append(", ");
            areasStr.append("}");

            first = false;
        }
        fieldLog(areasStr + "};");
    }



    public static void error(Exception e) {
        if (logger != null) {
            for (StackTraceElement el: e.getStackTrace()) {
                try {
                    logger.write("  " + el + "\n");

                } catch (IOException e1) {
                    //ignore
                }
            }
        }
    }

    public static void logTime(List<Time.TimeAction> actions, long overall) {
        if (logger != null && !actions.isEmpty()) {
            StringBuilder bld = new StringBuilder();
            for (Time.TimeAction action: actions) {
                bld.append(action.type).append(",").append(action.act).append(",").append(action.duration).append("\n");
            }
            bld.append("TOTAL,,").append(overall);
            try {
                logger.write(bld + "\n");
            } catch (IOException e) {
                //ignore
            }
        }
    }
}

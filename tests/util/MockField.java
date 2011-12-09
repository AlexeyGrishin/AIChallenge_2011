package util;

import bot2.Logger;
import bot2.ai.Ant;
import bot2.ai.areas.Areas;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MockField extends Field {

    private Map<Character, List<FieldPoint>> markedPoints = new HashMap<Character, List<FieldPoint>>();

    public MockField(String[] map) {
        super(map.length, map[0].length());
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                char c = map[row].charAt(col);
                Item type = Item.find(c);
                if (type != null) {
                    this.setItem(col, row, type);
                }
                getMarkedPoints(c).add(new FieldPoint(col, row));
            }
        }
    }

    public MockField(int rows, int cols) {
        super(rows, cols);
    }

    public MockField(String file, int nr) throws IOException {
        this(readFromFile(file, nr));
    }

    private static String[] readFromFile(String file, int nr) throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(file));
        List<String> sts = new ArrayList<String>();
        String s;
        int curNr = 0;
        while ((s = rdr.readLine()) != null) {
            sts.add(s);
            if (s.equals("")) {
                if (curNr == nr) break;
                curNr++;
                sts.clear();
            }

        }
        return convert(sts);
    }

    private static String[] convert(List<String> strings) {
        if (strings.get(0).startsWith("rows")) {
            strings = convertOfficialMaps(strings);
        }
        return strings.toArray(new String[strings.size()]);
    }

    private static List<String> convertOfficialMaps(List<String> strings) {
        //remove 3 first rows
        strings.remove(0);
        strings.remove(0);
        strings.remove(0);
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, strings.get(i)
                    .replaceAll("%", Item.WATER.getChar() + "")
                    .replaceAll("0", "+")
                    .replaceAll("m ", ""));
        }
        return strings;
    }

    public FieldPoint getMarkedPoint(char c) {
        List<FieldPoint> fieldPoints = getMarkedPoints(c);

        return fieldPoints.isEmpty() ? null : fieldPoints.get(0);
    }

    public List<FieldPoint> getMarkedPoints(char c) {
        List<FieldPoint> fieldPoints = markedPoints.get(c);
        if (fieldPoints == null) {
            fieldPoints = new ArrayList<FieldPoint>();
            markedPoints.put(c, fieldPoints);
        }
        return fieldPoints;
    }

    public void logMap() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                Character el = getItem(col, row).getChar();
                if (el == null) {
                    el = ' ';
                }
                System.out.print(el);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void logAreasMap() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                Character el = (char)('0' + getArea(col, row));
                if (el == '0') {
                    Item item = getItem(col, row);
                    if (item == Item.WATER)
                        el = item.getChar();
                    else
                        el = ' ';
                }
                System.out.print(el);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void checkAllCovered(double acceptableNotCovered, String mapFile, Areas areas) {
        List<FieldPoint> notCoveredAreas = new ArrayList<FieldPoint>();
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                if (getArea(col, row) == 0 && getItem(col, row) != Item.WATER) {
                    notCoveredAreas.add(new FieldPoint(col, row));
                }
            }
        }
        if (getCols() * getRows() * acceptableNotCovered < notCoveredAreas.size()) {
            logPoints(notCoveredAreas);
            Logger.logState(this, mapFile, areas, Collections.<Ant>emptyList());

            fail(mapFile + ": " + "Expected that all points are coverred, but following are not: " + notCoveredAreas);
        }
    }

    public void checkAreasAmount(int areas, int viewRadius, double overhead, String mapFile) {
        int desiredAreas = (getCols() * getRows() / ((viewRadius/2) * (viewRadius/2)));
        double realOverhead = (areas - desiredAreas) / desiredAreas;
        assertTrue(mapFile + ": " + "Desired: " + desiredAreas + " +- " + (desiredAreas * overhead) + ", but actual: " + areas, areas < desiredAreas || (realOverhead < overhead));
    }

    public void logPoints(List<FieldPoint> points) {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                Item item = getItem(col, row);
                char el = ' ';
                if (item == Item.WATER)
                    el = item.getChar();
                else if (points.contains(new FieldPoint(col, row))) {
                    el = '!';
                }

                System.out.print(el);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void makeAllVisible() {
        replaceAll(Item.UNKNOWN, Item.LAND);
    }

    public void replaceAll(Item from, Item to) {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                replace(FieldPoint.point(col, row), from, to);
            }
        }
    }

}

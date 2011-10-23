package util;

import bot.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MockAnts extends Ants {

    static Map<Character, Ilk> types = new HashMap<Character, Ilk>() {
        {
            put('%', Ilk.WATER);
            put('a', Ilk.MY_ANT);
            put('*', Ilk.FOOD);
            put('b', Ilk.ENEMY_ANT);
        }
    };
    static Map<Ilk, Character> backTypes = new HashMap<Ilk, Character>() {
        {
            put(Ilk.WATER, '%');
            put(Ilk.MY_ANT, 'a');
            put(Ilk.FOOD, '*');
            put(Ilk.ENEMY_ANT, 'b');
            put(Ilk.LAND, '.');
        }
    };

    private Map<Tile, Aim> orders = new HashMap<Tile, Aim>();
    private List<Tile> visited = new ArrayList<Tile>();

    public MockAnts (String file) throws IOException {
        this(loadMap(file));
    }
    public MockAnts (int rows, int cols) throws IOException {
        this(generateEmpty(rows, cols));
    }

    private static String[] generateEmpty(int rows, int cols) {
        String[] map = new String[rows];
        String filler = "";
        for (int i = 0; i < cols; i++) filler += ".";
        for (int i = 0; i < rows; i++) map[i] = filler;
        return map;
    }

    private static String[] loadMap(String file) throws IOException {
        BufferedReader rdr = new BufferedReader(new FileReader(file));
        List<String> sts = new ArrayList<String>();
        String s;
        while ((s = rdr.readLine()) != null) {
            sts.add(s);
        }
        return sts.toArray(new String[sts.size()]);
    }

    public MockAnts (String[] map) {
        super(0, 0, map.length, map[0].length(), 9999, 55, 5, 1);
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                Ilk type = types.get(map[row].charAt(col));
                if (type != null) {
                    update(type, new Tile(row, col));
                }
            }
        }
    }

    public void assertOrder(Tile ant, Tile to) {
        assertOrder(ant, getDirections(ant, to).get(0));
    }

    @Override
    public void issueOrder(Tile myAnt, Aim direction) {
        super.issueOrder(myAnt, direction);
        orders.put(myAnt, direction);
        visited.add(myAnt);
    }

    public void assertOrder(Tile ant, Aim to) {
        assertEquals(to, orders.remove(ant));
    }

    public void assertNoOrder(Tile ant) {
        assertNull(orders.remove(ant));
    }

    public void assertNoMoOrders() {
        assertEquals(new HashMap(), orders);
    }

    @Override
    protected void createLog() {
        //do nothing
    }

    @Override
    public void log(String string) {
        System.out.println(string);
    }

    public void logTraversingMap() {
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                Character el = backTypes.get(this.map[row][col]);
                if (el == null) {
                    el = ' ';
                }
                if (this.map[row][col] == Ilk.LAND && visited.contains(new Tile(row, col))) {
                    el = '#';
                }
                System.out.print(el);
            }
            System.out.println();
        }

    }

    public void assertOurAnt(int row, int col) {
        assertEquals(Ilk.MY_ANT, getIlk(new Tile(row, col)));
    }
}

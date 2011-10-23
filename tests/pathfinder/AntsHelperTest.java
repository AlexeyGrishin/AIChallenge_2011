package pathfinder;

import bot.Tile;
import org.junit.Before;
import org.junit.Test;
import util.MockAnts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class AntsHelperTest {

    private static final double DELTA = 0.01;
    private MockAnts ants;
    private AntsHelper helper;

    @Before
    public void setUp() throws IOException {
        ants = new MockAnts(20, 20);
        helper = new AntsHelper(ants);
    }

    @Test
    public void testGetQuickDistance_same() throws IOException {
        assertEquals(0, helper.getQuickDistanceBetween(t(10,10), t(10,10)));
    }

    @Test
    public void testGetQuickDistance_near() throws IOException {
        assertEquals(1, helper.getQuickDistanceBetween(t(10,10), t(10,11)));
        assertEquals(1, helper.getQuickDistanceBetween(t(10,10), t(11,10)));
        assertEquals(1, helper.getQuickDistanceBetween(t(10,10), t(9,10)));
        assertEquals(1, helper.getQuickDistanceBetween(t(10,10), t(10, 9)));
    }


    @Test
    public void testGetQuickDistance_diag() throws IOException {
        assertEquals(2, helper.getQuickDistanceBetween(t(10,10), t(11,11)));
        assertEquals(2, helper.getQuickDistanceBetween(t(10,10), t(9,11)));
        assertEquals(2, helper.getQuickDistanceBetween(t(10,10), t(11,9)));
        assertEquals(2, helper.getQuickDistanceBetween(t(10,10), t(9,9)));
    }

    @Test
    public void testGetQuickDistance_overlapp() {
        assertEquals(1, helper.getQuickDistanceBetween(t(0, 0), t(19,0)));
        assertEquals(2, helper.getQuickDistanceBetween(t(0, 0), t(19,19)));
    }

    double TSQRT = Math.sqrt(2);

    @Test
    public void testGetDistance_same() throws IOException {
        assertEquals(0d, helper.getDistanceBetween(t(10, 10), t(10, 10)));
    }

    @Test
    public void testGetDistance_near() throws IOException {
        assertEquals(1d, helper.getDistanceBetween(t(10,10), t(10,11)));
        assertEquals(1d, helper.getDistanceBetween(t(10,10), t(11,10)));
        assertEquals(1d, helper.getDistanceBetween(t(10,10), t(9,10)));
        assertEquals(1d, helper.getDistanceBetween(t(10,10), t(10,9)));
    }


    @Test
    public void testGetDistance_diag() throws IOException {
        assertEquals(TSQRT, helper.getDistanceBetween(t(10,10), t(11,11)), DELTA);
        assertEquals(TSQRT, helper.getDistanceBetween(t(10,10), t(9,11)), DELTA);
        assertEquals(TSQRT, helper.getDistanceBetween(t(10,10), t(11,9)), DELTA);
        assertEquals(TSQRT, helper.getDistanceBetween(t(10,10), t(9,9)), DELTA);
    }

    @Test
    public void testGetDistance_overlapp() {
        assertEquals(1d, helper.getDistanceBetween(t(0, 0), t(19,0)));
        assertEquals(TSQRT, helper.getDistanceBetween(t(0, 0), t(19,19)), DELTA);
    }

    @Test
    public void testGetNearesetCells() {
        List<Tile> cells = new ArrayList<Tile>((Collection<? extends Tile>) helper.getNearestCells(new Tile(0, 0)));
        assertTrue(cells.contains(t(0, 1)));
        assertTrue(cells.contains(t(1, 0)));
        assertTrue(cells.contains(t(0, 19)));
        assertTrue(cells.contains(t(19, 0)));
    }

    private static Tile t(int row, int col) {
        return new Tile(row, col);
    }
}

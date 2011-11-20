package logic;

import bot.Ilk;
import bot.Tile;
import bot.brain.View;
import org.junit.Test;
import util.MockAnts;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ViewTest {

    @Test
    public void shallDetectEnemiesInRadiusOnly() throws IOException {
        MockAnts ants = createMapOfEnemiesWithMyAntInCenter();
        View view = new View(ants, new Tile(5,5), 4);
        assertEquals(Arrays.asList(
                new Tile(5,3),
                new Tile(4,4), new Tile(5,4), new Tile(6,4),
                new Tile(3,5),new Tile(4,5), new Tile(6,5), new Tile(7,5),
                new Tile(4,6), new Tile(5,6), new Tile(6,6),
                new Tile(5,7)
        ), view.getEnemies());
        assertEquals(0, view.getFriendsCount());

        ants.logTraversingMap();
    }

    @Test
    public void shallDetectEnemiesAndFriendsInRadiusOnly() throws IOException {
        MockAnts ants = createMapOfEnemiesWithMyAntInCenter();
        View view = new View(ants, new Tile(6,6), 4);
        assertEquals(11, view.getEnemiesCount());
        assertEquals(1, view.getFriendsCount());

        ants.logTraversingMap();
    }

    @Test
    public void shallDetectFriendsInRadiusOnly() throws IOException {
        MockAnts ants = createMapOfFriendsWithMyAntInCenter();
        View view = new View(ants, new Tile(5,5), 3);
        assertEquals(Arrays.asList(
                new Tile(4,4), new Tile(5,4), new Tile(6,4),
                new Tile(4,5), new Tile(6,5),
                new Tile(4,6), new Tile(5,6), new Tile(6,6)
        ), view.getFriends());
        assertEquals(0, view.getEnemiesCount());

        ants.logTraversingMap();
    }

    private MockAnts createMapOfFriendsWithMyAntInCenter() throws IOException {
        return createMapWithMyAntInCenter(Ilk.MY_ANT);
    }

    private MockAnts createMapOfEnemiesWithMyAntInCenter() throws IOException {
        return createMapWithMyAntInCenter(Ilk.ENEMY_ANT);
    }

    private MockAnts createMapWithMyAntInCenter(Ilk filler) throws IOException {
        MockAnts ants = new MockAnts(10, 10);
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                boolean isCenter = col == 5 && row == 5;
                ants.update(isCenter ? Ilk.MY_ANT : filler, new Tile(row, col));
            }
        }
        return ants;

    }
}

package logic;

import bot.Bot;
import bot.brain.BrainBot;
import bot.Tile;
import bot.brain.Strategy;
import org.junit.Before;
import org.junit.Test;
import util.MockAnts;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class FoodHarvestingTest {

    private BrainBot bot;
    private MockAnts ants;

    @Before
    public void setUp() throws IOException {
        bot = new BrainBot(new Strategy());
        ants = new MockAnts(5, 5);
        bot.setAnts(ants);
    }

    @Test
    public void testHarvestFoodIfNear() {
        bot.beforeUpdate();
        bot.addFood(3, 4);
        bot.addAnt(3, 0, 0);
        bot.afterUpdate();

        bot.doTurn();
        ants.logTraversingMap();
        ants.assertOrder(new Tile(3,0), new Tile(3, 4));
    }


    @Test
    public void testSkipFoodIfNotReachable() throws IOException {
        ants = new MockAnts("tests/logic/foodisnotvisible.map");
        bot = new BrainBot(new Strategy());
        bot.setAnts(ants);

        GameMock mock = new GameMock(bot, ants);
        mock.turn();
        for (int i = 0; i < 50; i++) {
            mock.turn();
        }

        ants.logTraversingMap();

        assertFalse(bot.getTheAnts().get(0).isHarvesting());

    }

}

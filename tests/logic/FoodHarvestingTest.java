package logic;

import bot.Aim;
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

    @Test
    public void shallGoDirectlyToFood() throws IOException {
        ants = new MockAnts("tests/logic/harvest2.map");
        bot = new BrainBot(new Strategy());
        bot.setAnts(ants);

        GameMock mock = new GameMock(bot, ants);
        Tile ant = ants.getMyAnts().iterator().next();
        for (int i = 0; i < 6; i++) {
            mock.turn();
            ants.assertOrder(ant, Aim.EAST);
            ant = ants.getTile(ant, Aim.EAST);
        }

        ants.logTraversingMap();

        ants.assertOurAnt(5, 14);

    }

    @Test
    public void shallSelectNearestForAll() throws IOException {
        ants = new MockAnts("tests/logic/harvest3.map");
        bot = new BrainBot(new Strategy());
        bot.setAnts(ants);

        GameMock mock = new GameMock(bot, ants);
        for (int i = 0; i < 3; i++) {
            mock.turn();
        }
        ants.logTraversingMap();

        ants.assertOurAnt(2, 9);
        ants.assertOurAnt(3, 10);
    }

    @Test
    public void shallSelectNearest() throws IOException {
        ants = new MockAnts(10, 10);
        bot = new BrainBot(new Strategy());
        bot.setAnts(ants);
        bot.addAnt(5,5,0);
        bot.addFood(0,0);
        bot.addFood(1,1);
        bot.addFood(6,6);

        GameMock mock = new GameMock(bot, ants);
        for (int i = 0; i < 2; i++) {
            mock.turn();
        }
        ants.logTraversingMap();

        ants.assertOurAnt(6, 6);
    }

}

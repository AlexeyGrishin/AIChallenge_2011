package logic;

import bot.Aim;
import bot.Tile;
import bot.brain.BrainBot;
import bot.brain.Group;
import bot.brain.Strategy;
import org.junit.Before;
import org.junit.Test;
import util.MockAnts;

import java.io.IOException;
import java.util.ArrayList;

public class GroupTest {

    private MockAnts ants;
    private BrainBot bot;
    private GameMock game;
    private Group group;

    @Before
    public void setUp() throws IOException {
        ants = new MockAnts(20, 20);
        bot = new BrainBot(new Strategy());
        bot.setAnts(ants);
        game = new GameMock(bot, ants);
        group = new Group();
        group.setField(ants);
    }

    @Test
    public void turnToEnemy_north_noMoevementRequired() {
        Tile b1 = bot.addAnt(19, 2, 0);
        Tile b2 = bot.addAnt(19, 4, 0);
        bot.addAnt(15, 3, 1);

        doTurnToEnemies();

        ants.assertNoMoOrders();
    }

    @Test
    public void turnToEnemy_north_stepLeftRequired() {
        Tile b1 = bot.addAnt(19, 2, 0);
        Tile b2 = bot.addAnt(19, 4, 0);
        bot.addAnt(15, 2, 1);

        doTurnToEnemies();

        ants.assertOrder(b2, Aim.WEST);
        ants.assertNoMoOrders();
    }

    @Test
    public void turnToEnemy_north_stepRightRequired() {
        Tile b1 = bot.addAnt(19, 2, 0);
        Tile b2 = bot.addAnt(19, 4, 0);
        bot.addAnt(15, 4, 1);

        doTurnToEnemies();

        ants.assertOrder(b1, Aim.EAST);
        ants.assertNoMoOrders();
    }

    @Test
    public void turnToEnemy_north_stepUpRequired() {
        Tile b1 = bot.addAnt(18, 2, 0);
        Tile b2 = bot.addAnt(19, 3, 0);
        bot.addAnt(15, 3, 1);

        doTurnToEnemies();

        ants.assertOrder(b2, Aim.NORTH);
        ants.assertNoMoOrders();
    }


    @Test
    public void turnToEnemy_north_diagonal() {
        Tile b1 = bot.addAnt(18, 3, 0);
        Tile b2 = bot.addAnt(19, 3, 0);
        bot.addWater(19, 4);
        bot.addAnt(15, 3, 1);

        doTurnToEnemies();

        ants.assertOrder(b2, Aim.WEST);
        ants.assertNoMoOrders();

    }

    @Test
    public void turnToEnemy_ne_stepUpRequired() {
        Tile b1 = bot.addAnt(19, 2, 0);
        Tile b2 = bot.addAnt(19, 4, 0);
        bot.addAnt(15, 9, 1);

        doTurnToEnemies();

        ants.assertOrder(b1, Aim.NORTH);
        ants.assertNoMoOrders();

    }

    @Test
    public void turnToEnemy_severalAnts_onlyOneShallMove() {
        Tile b1 = bot.addAnt(18, 3, 0);
        Tile b2 = bot.addAnt(19, 2, 0);
        Tile b3 = bot.addAnt(19, 5, 0);
        Tile b4 = bot.addAnt(19, 6, 0);
        bot.addAnt(15, 3, 1);

        doTurnToEnemies();

        ants.assertOrder(b2, Aim.NORTH);
        ants.assertNoMoOrders();

    }


    @Test
    public void turnToEnemy_severalAnts_severalEnemies() {
        Tile b1 = bot.addAnt(18, 3, 0);
        Tile b2 = bot.addAnt(19, 2, 0);
        Tile b3 = bot.addAnt(19, 5, 0);
        Tile b4 = bot.addAnt(19, 7, 0);
        bot.addAnt(15, 3, 1);
        bot.addAnt(15, 7, 1);

        doTurnToEnemies();

        ants.assertOrder(b2, Aim.NORTH);
        ants.assertOrder(b3, Aim.EAST);
        ants.assertNoMoOrders();

    }

    private void doTurnToEnemies() {
        group.turnToEnemies(bot.getTheAnts(), new ArrayList<Tile>(ants.getEnemyAnts()));
    }
}

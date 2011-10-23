package logic;

import bot.Bot;
import bot.Ilk;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.BrainBot;
import bot.brain.Order;
import bot.brain.Strategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import util.MockAnts;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HillRushingTest {

    private BrainBot bot;
    private MockAnts ants;
    private Tile target;

    @Before
    public void setUp() throws IOException {
        Strategy strategy = mock(Strategy.class);
        when(strategy.getCountOfRushers(Matchers.<List<Ant>>any())).thenReturn(1);
        bot = new BrainBot(strategy);
        ants = new MockAnts("tests/logic/hill.map");
        bot.setAnts(ants);
        bot.beforeUpdate();
        bot.addAnt(10, 102, 0);
        target = new Tile(106, 37);
        bot.addHill(target.getRow(), target.getCol(), 1);
        assertEquals(Ilk.LAND, ants.getIlk(target));
        bot.afterUpdate();
    }

    @Test
    public void testRushTheHill() {
        int maxOrdersCount = 150;
        Tile antPos = ants.getMyAnts().iterator().next();
        for (int i = 0; i < maxOrdersCount; i++) {
            bot.doTurn();
            Set<Order> orders = ants.getOrders();
            if (!orders.isEmpty()) {
                Order order = orders.iterator().next();
                antPos = ants.getTile(antPos, order.getAim());
            }

            if (antPos.equals(target)) {
                break;
            }

            bot.beforeUpdate();
            bot.addAnt(antPos.getRow(), antPos.getCol(), 0);
            bot.afterUpdate();
        }
        ants.logTraversingMap();
        assertEquals(target, antPos);


    }

    @Test
    public void rushTheHill_nearestOnes() throws IOException {
        Strategy strategy = mock(Strategy.class);
        when(strategy.getCountOfRushers(Matchers.<List<Ant>>any())).thenReturn(2);
        bot = new BrainBot(strategy);
        ants = new MockAnts("tests/logic/hill2.map");
        bot.setAnts(ants);
        GameMock mock = new GameMock(bot, ants);
        mock.addEnemyHill(new Tile(11, 8));
        mock.update();
        List<Ant> listOfAnts = bot.getTheAnts();
        Ant firstRusher = getAntOn(listOfAnts, 5, 6);
        Ant secondRusher = getAntOn(listOfAnts, 5, 7);
        Ant firstHarvester = getAntOn(listOfAnts, 4, 6);
        Ant secondHarvester = getAntOn(listOfAnts, 4, 7);
        mock.doTurn();
        assertTrue(firstRusher.isRushing());
        assertTrue(secondRusher.isRushing());
        assertFalse(firstHarvester.isRushing());
        assertFalse(secondHarvester.isRushing());

    }

    private Ant getAntOn(List<Ant> listOfAnts, int row, int col) {
        for (Ant ant: listOfAnts) {
            if (ant.getPosition().equals(new Tile(row, col))) {
                return ant;
            }
        }
        fail("There is no ant on " + row + ", " + col);
        return null;
    }

}

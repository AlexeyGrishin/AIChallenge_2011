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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HillRushingTest {

    private Bot bot;
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

}

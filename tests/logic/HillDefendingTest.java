package logic;

import bot.Aim;
import bot.Ilk;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.BrainBot;
import bot.brain.Strategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.MockAnts;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class HillDefendingTest {

    private static final Tile CENTER = new Tile(3, 3);
    @Mock Strategy strategy;
    private MockAnts ants;
    private BrainBot bot;
    private GameMock game;
    private Tile enemy;
    private static final Tile SECOND_CENTER = new Tile(9, 9);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidPositions_simple() throws IOException {
        prepare("tests/logic/defence1.map");

        for (int i = 0; i < 10; i++) {
            game.turn();
        }

        ants.logTraversingMap();
        ants.assertOurAnt(2, 2);
        ants.assertOurAnt(2, 4);
        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(4, 4);
    }

    @Test
    public void testValidPositions_inCorner() throws IOException {
        prepare("tests/logic/defence2.map");
        for (int i = 0; i < 8; i++) {
            game.turn();
        }

        ants.logTraversingMap();
        ants.assertOurAnt(4, 2);


    }

    @Test
    public void testValidPositions_onWall() throws IOException {
        prepare("tests/logic/defence3.map");
        int born = 0;
        for (int i = 0; i < 30; i++) {
            if (ants.getIlk(CENTER) != Ilk.MY_ANT && born < 11) {
                game.followAnt(CENTER);
                born++;
            }
            game.turn();
        }

        ants.logTraversingMap();
        assertEquals("Expected that all ants will be born", 11, born);

        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(4, 4);
    }


    @Test
    public void shallNotMoveFromPositions()throws IOException {
        prepare("tests/logic/defence4.map");
        game.turn();
        ants.logTraversingMap();
        ants.assertNoMoOrders();
    }

    @Test
    public void shallMoveTowardEnemy()throws IOException {
        prepare("tests/logic/defence5.map");
        enemy = new Tile(8, 2);
        game.setEnemy(enemy);
        doTurnAndCheckAntOn(4, 3);
        moveEnemy(Aim.NORTH);
        doTurnAndCheckAntOn(4, 3);
        moveEnemy(Aim.EAST);
        moveEnemy(Aim.EAST);
        moveEnemy(Aim.EAST);
        doTurnAndCheckAntOn(5, 2);
        moveEnemy(Aim.NORTH);
        moveEnemy(Aim.NORTH);
        doTurnAndCheckAntOn(3, 4);

        //and now we need to return back as there is no enemy. I expect
        //to have 2 turns for it
        game.removeEnemy(enemy);
        game.turn();
        game.turn();
        game.turn();
        ants.logTraversingMap();

        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(4, 4);
        ants.assertOurAnt(2, 4);
        ants.assertOurAnt(2, 2);
    }

    @Test
    public void shallGuardBothHills() throws IOException {
        prepare("tests/logic/defence6.map");
        game.addHill(SECOND_CENTER);
        game.turn();
        ants.assertNoMoOrders();
        game.followAnt(SECOND_CENTER);
        game.turn();
        ants.assertOrder(SECOND_CENTER, Aim.NORTH);
        game.turn();
        ants.assertOrder(ants.getTile(SECOND_CENTER, Aim.NORTH), Aim.WEST);
        game.followAnt(SECOND_CENTER);
        game.turn();
        game.followAnt(SECOND_CENTER);
        game.turn();
        game.followAnt(SECOND_CENTER);
        game.turn();
        game.turn();
        game.turn();
        game.turn();
        ants.assertOurAnt(8, 8);
        ants.assertOurAnt(8, 10);
        ants.assertOurAnt(10, 8);
        ants.assertOurAnt(10, 10);
        ants.logTraversingMap();

    }

    @Test
    public void shallReduceCountOfGuardIfNoDanger() throws IOException {
        prepare("tests/logic/defence7.map");
        when(strategy.getCountOfGuards(anyListOf(Ant.class), anyInt())).thenReturn(12);
        game.setEnemy(new Tile(8, 3));
        game.turn();
        game.followAnt(new Tile(5, 2));
        game.followAnt(new Tile(5, 3));
        game.followAnt(new Tile(5,4));
        game.turn();
        Distribution distribution = new Distribution().invoke();
        assertEquals(7, distribution.getGuarding());
        assertEquals(0, distribution.getHarvesting());
        game.removeEnemy(new Tile(8,3));
        game.turn();
        distribution = new Distribution().invoke();
        assertEquals(4, distribution.getGuarding());
        assertEquals(3, distribution.getHarvesting());
    }

    @Test
    public void shallGuardFromRush() throws IOException {
        prepare("tests/logic/defence8.map");
        when(strategy.getCountOfGuards(anyListOf(Ant.class), anyInt())).thenReturn(12);
        for (int i = 0; i < 10; i++) {
            game.turn();
            game.moveEnemies(Aim.NORTH);
            ants.logTraversingMap();
            game.calculateDeath();
            ants.assertNoEnemyAnt(CENTER);
        }
    }

    private void doTurnAndCheckAntOn(int row, int col) {
        game.turn();
        ants.logTraversingMap();
        ants.assertOurAnt(row, col);
    }

    private void moveEnemy(Aim dir) {
        game.removeEnemy(enemy);
        enemy = ants.getTile(enemy, dir);
        game.setEnemy(enemy);
    }


    private void prepare(String map) throws IOException {
        ants = new MockAnts(map);
        bot = new BrainBot(strategy);
        when(strategy.getCountOfGuards(anyListOf(Ant.class), eq(0))).thenReturn(12);
        bot.setAnts(ants);
        game = new GameMock(bot, ants);
        game.addHill(CENTER);
    }


    private class Distribution {
        private int harvesting;
        private int guarding;

        public int getHarvesting() {
            return harvesting;
        }

        public int getGuarding() {
            return guarding;
        }

        public Distribution invoke() {
            harvesting = 0;
            guarding = 0;
            for (Ant ant: bot.getTheAnts()) {
                if (bot.isGuard(ant)) {
                    guarding++;
                }
                else {
                    harvesting++;
                }
            }
            return this;
        }
    }
}

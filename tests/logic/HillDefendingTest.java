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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class HillDefendingTest {

    private static final Tile CENTER = new Tile(3, 3);
    @Mock Strategy strategy;
    private MockAnts ants;
    private BrainBot bot;
    private GameMock game;
    private Tile enemy;

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
        ants.assertOurAnt(1, 2);
        ants.assertOurAnt(1, 4);
        ants.assertOurAnt(2, 1);
        ants.assertOurAnt(2, 2);
        ants.assertOurAnt(2, 4);
        ants.assertOurAnt(2, 5);
        ants.assertOurAnt(4, 1);
        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(4, 4);
        ants.assertOurAnt(4, 5);
        ants.assertOurAnt(5, 2);
        ants.assertOurAnt(5, 4);
    }

    @Test
    public void testValidPositions_inCorner() throws IOException {
        prepare("tests/logic/defence2.map");
        for (int i = 0; i < 8; i++) {
            game.turn();
        }

        ants.logTraversingMap();
        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(5, 2);
        ants.assertOurAnt(4, 1);

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
        ants.assertOurAnt(1, 2);
        ants.assertOurAnt(1, 4);
        ants.assertOurAnt(4, 1);
        ants.assertOurAnt(4, 2);
        ants.assertOurAnt(4, 4);
        ants.assertOurAnt(4, 5);
        ants.assertOurAnt(5, 2);
        ants.assertOurAnt(5, 4);

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
        doTurnAndCheckAntOn(4,3);
        moveEnemy(Aim.NORTH);
        doTurnAndCheckAntOn(4,3);
        moveEnemy(Aim.EAST);
        moveEnemy(Aim.EAST);
        moveEnemy(Aim.EAST);
        doTurnAndCheckAntOn(5,2);
        moveEnemy(Aim.NORTH);
        moveEnemy(Aim.NORTH);
        doTurnAndCheckAntOn(3,4);

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
        when(strategy.getCountOfGuards(anyListOf(Ant.class))).thenReturn(12);
        bot.setAnts(ants);
        game = new GameMock(bot, ants);
        game.addHill(CENTER);
    }


}

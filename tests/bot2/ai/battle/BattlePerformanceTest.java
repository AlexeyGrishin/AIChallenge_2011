package bot2.ai.battle;

import bot2.Logger;
import bot2.ai.Ant;
import bot2.map.FieldPoint;
import com.sun.servicetag.SystemEnvironment;
import org.junit.BeforeClass;
import org.junit.Test;
import util.MockField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class BattlePerformanceTest extends BattleTestAbstract {

    @BeforeClass
    public static void t() throws IOException {
        Logger.init();
    }

    @Override
    protected MockField produceField() throws IOException {
        return new MockField("tests/bot2/ai/battle/battle.map", 0);
    }

    @Test//(timeout = 2000)
    public void performance() throws IOException, InterruptedException {
        //Thread.sleep(15000);
        when(strategy.getSingleBattleStrategy(anyListOf(FieldPoint.class), anyListOf(FieldPoint.class))).thenReturn(new SingleBattleStrategy(true, true, 10));
        battle.process(antsFromField());
    }


    private List<Ant> antsFromField() {
        List<Ant> ants = new ArrayList<Ant>();
        for (FieldPoint p: field.getMarkedPoints('a')) {
            ants.add(new Ant(p, mover, view));
        }
        return ants;
    }


}

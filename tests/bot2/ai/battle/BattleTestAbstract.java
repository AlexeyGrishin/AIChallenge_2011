package bot2.ai.battle;

import bot2.GameSettings;
import bot2.MoveHelper;
import bot2.ai.Ant;
import bot2.ai.GameStrategy;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.View;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import util.MockField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public abstract class BattleTestAbstract {
    public static final Answer<Object> MAX_DAMAGE = new Answer<Object>() {
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            List<BattleCase<FieldPoint>> cases = (List<BattleCase<FieldPoint>>) invocationOnMock.getArguments()[0];
            return BattleStrategy.maxDamage(cases);
        }
    };
    public static final Answer<Object> MIN_LOST = new Answer<Object>() {
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            List<BattleCase<FieldPoint>> cases = (List<BattleCase<FieldPoint>>) invocationOnMock.getArguments()[0];
            return BattleStrategy.minLost(cases);
        }
    };
    @Mock
    MoveHelper mover;
    @Mock
    View view;
    GameSettings settings;
    @Mock
    GameStrategy strategy;
    Battle battle;
    protected MockField field;

    @Before

    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(view.getEnemiesCount()).thenReturn(2);
        /*
        when(settings.getAttackRadius()).thenReturn(2);
        when(settings.getAttackRadius2()).thenReturn(5);*/
        settings = new GameSettings(5, 77, 500);
        field = produceField();
        field.makeAllVisible();
        battle = new Battle(settings, field, strategy);
        when(mover.canMoveTo(any(FieldPoint.class))).thenReturn(true);
        when(mover.move(any(FieldPoint.class), any(FieldPoint.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArguments()[1];
            }
        });
        when(strategy.getSingleBattleStrategy(anyListOf(FieldPoint.class), anyListOf(FieldPoint.class))).thenReturn(new SingleBattleStrategy(false, true, 10));
    }

    protected abstract MockField produceField() throws IOException;

    protected List<Ant> list(Ant... ants) {
        List<Ant> rants = new ArrayList<Ant>();
        Collections.addAll(rants, ants);
        return rants;
    }

    protected void createEnemyAnt(FieldPoint... points) {
        for (FieldPoint point: points)
            field.setItem(point, Item.ENEMY_ANT);
    }

    Ant createAnt(FieldPoint point) {
        field.setItem(point, Item.ANT);
        return new Ant(point, mover, view);
    }
}

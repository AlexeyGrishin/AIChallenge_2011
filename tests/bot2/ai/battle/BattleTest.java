package bot2.ai.battle;

import bot2.GameSettings;
import bot2.MoveHelper;
import bot2.ai.AI;
import bot2.ai.Ant;
import bot2.ai.GameStrategy;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import util.MockAnts;
import util.MockField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class BattleTest {

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
    @Mock MoveHelper mover;
    @Mock View view;
    @Mock GameSettings settings;
    @Mock GameStrategy strategy;
    Battle battle;
    private MockField field;

    @Before

    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(view.getEnemiesCount()).thenReturn(2);
        when(settings.getAttackRadius()).thenReturn(2);
        when(settings.getAttackRadius2()).thenReturn(5);
        field = new MockField(50, 50);
        field.makeAllVisible();
        battle = new Battle(settings, field, strategy);
        when(mover.canMoveTo(any(FieldPoint.class))).thenReturn(true);
        when(mover.move(any(FieldPoint.class), any(FieldPoint.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return invocationOnMock.getArguments()[1];
            }
        });
    }

    @Test
    public void twoAntsMaxDamage() {
        when(strategy.select(any(List.class), any(List.class), any(List.class))).thenAnswer(MAX_DAMAGE);
        Ant ant = createAnt(FieldPoint.point(10, 10));
        createEnemyAnt(FieldPoint.point(10, 6));
        List<Ant> ants = list(ant);
        battle.process(ants);
        verify(mover).move(FieldPoint.point(10, 10), FieldPoint.point(10, 9));
    }

    @Test
    public void twoAntsMinLost() {
        when(strategy.select(any(List.class), any(List.class), any(List.class))).thenAnswer(MIN_LOST);
        Ant ant = createAnt(FieldPoint.point(10, 10));
        createEnemyAnt(FieldPoint.point(10, 6));
        List<Ant> ants = list(ant);
        battle.process(ants);
        verify(mover, never()).move(any(FieldPoint.class), any(FieldPoint.class));
    }

    @Test
    public void twoAntsCloser_MinLost() {
        when(strategy.select(any(List.class), any(List.class), any(List.class))).thenAnswer(MIN_LOST);
        Ant ant = createAnt(FieldPoint.point(10, 10));
        createEnemyAnt(FieldPoint.point(10, 7));
        List<Ant> ants = list(ant);
        battle.process(ants);
        verify(mover).move(FieldPoint.point(10, 10), FieldPoint.point(10, 11));
    }


    @Test
    public void twoOurAnts_near() {
        when(strategy.select(any(List.class), any(List.class), any(List.class))).thenAnswer(MAX_DAMAGE);
        Ant ant1 = createAnt(FieldPoint.point(9, 10));
        Ant ant2 = createAnt(FieldPoint.point(11, 10));
        createEnemyAnt(FieldPoint.point(10, 7));
        List<Ant> ants = list(ant1, ant2);
        battle.process(ants);
        verify(mover).move(FieldPoint.point(10, 10), FieldPoint.point(10, 9));
        verify(mover).move(FieldPoint.point(11, 10), FieldPoint.point(11, 9));
    }

    @Test
    public void twoOurAnts_around_maxDamage() {
        when(strategy.select(any(List.class), any(List.class), any(List.class))).thenAnswer(MAX_DAMAGE);
        Ant ant1 = createAnt(FieldPoint.point(13, 10));
        Ant ant2 = createAnt(FieldPoint.point(7, 10));
        createEnemyAnt(FieldPoint.point(10, 10));
        List<Ant> ants = list(ant1, ant2);
        battle.process(ants);
        verify(mover).move(FieldPoint.point(13, 10), FieldPoint.point(12, 10));
        verify(mover).move(FieldPoint.point(7, 10), FieldPoint.point(8, 10));

    }

    private List<Ant> list(Ant... ants) {
        List<Ant> rants = new ArrayList<Ant>();
        Collections.addAll(rants, ants);
        return rants;
    }

    private void createEnemyAnt(FieldPoint point) {
        field.setItem(point, Item.ENEMY_ANT);
    }

    Ant createAnt(FieldPoint point) {
        field.setItem(point, Item.ANT);
        return new Ant(point, mover, view);
    }
}

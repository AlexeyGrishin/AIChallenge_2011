package bot2.ai.battle;

import bot2.ai.Ant;
import bot2.map.FieldPoint;
import org.junit.Test;
import util.MockField;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class BattleTest extends BattleTestAbstract {

    protected MockField produceField() {
        return new MockField(150, 150);
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
        verify(mover).move(FieldPoint.point(9, 10), FieldPoint.point(8, 10));
        verify(mover).move(FieldPoint.point(11, 10), FieldPoint.point(10, 10));
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
        verify(mover).move(FieldPoint.point(7, 10), FieldPoint.point(7, 11));
    }

}

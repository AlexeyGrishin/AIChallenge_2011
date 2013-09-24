package bot2.logic;

import bot2.MoveHelper;
import bot2.ai.Ant;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.View;
import bot2.map.VisibleAreaPathHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import pathfinder.PointHelper;
import util.MockField;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AntPathFinding {


    private MoveHelper moveHelper;
    private View view;
    private MockField field;
    private PointHelper<FieldPoint> pathHelper;

    @Before
    public void setUp() {
        moveHelper = mock(MoveHelper.class);
        view = mock(View.class);
        when(view.isNear(any(FieldPoint.class))).thenReturn(true);
        field = new MockField(20, 20);
        field.makeAllVisible();
        pathHelper = new VisibleAreaPathHelper(view, field);
        when(view.producePointHelper()).thenReturn(pathHelper);
        when(moveHelper.move(any(FieldPoint.class), any(FieldPoint.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                field.setItem((FieldPoint)invocationOnMock.getArguments()[0], Item.LAND);
                field.setItem((FieldPoint)invocationOnMock.getArguments()[1], Item.ANT);
                return invocationOnMock.getArguments()[1];
            }
        });
        when(moveHelper.canMoveTo(any(FieldPoint.class))).thenReturn(true);
        when(moveHelper.kickOurAntAt(any(FieldPoint.class))).thenReturn(MoveHelper.KickResult.KICKED_OUT);
    }

    @Test
    public void path_1() {
        Ant ant = new Ant(FieldPoint.point(8, 10), moveHelper, view);
        ant.doWalkToPoint(FieldPoint.point(12, 10));
        doMove(ant);
        doMove(ant);
        doMove(ant);
        verify(moveHelper).move(FieldPoint.point(8, 10), FieldPoint.point(9, 10));
        verify(moveHelper).move(FieldPoint.point(9, 10), FieldPoint.point(10, 10));
        verify(moveHelper).move(FieldPoint.point(10, 10), FieldPoint.point(11, 10));
        verify(moveHelper).move(FieldPoint.point(11, 10), FieldPoint.point(12, 10));
    }

    @Test
    public void intersection_1() {
        when(view.isNear(any(FieldPoint.class))).thenReturn(false);
        Ant ant1 = new Ant(FieldPoint.point(8, 10), moveHelper, view);
        Ant ant2 = new Ant(FieldPoint.point(11, 10), moveHelper, view);
        ant1.doWalkToPoint(FieldPoint.point(11, 10));
        ant2.doWalkToPoint(FieldPoint.point(8, 10));
        syncField(ant1, ant2);
        verify(moveHelper).move(FieldPoint.point(8, 10), FieldPoint.point(9, 10));
        verify(moveHelper).move(FieldPoint.point(11, 10), FieldPoint.point(10, 10));
        when(view.isNear(any(FieldPoint.class))).thenReturn(true);
        when(moveHelper.canMoveTo(FieldPoint.point(10, 10))).thenReturn(false);
        doMove(ant1);
        verify(moveHelper).move(FieldPoint.point(9, 10), FieldPoint.point(9, 9));
        doMove(ant2);
        verify(moveHelper).move(FieldPoint.point(10, 10), FieldPoint.point(9, 10));
        doMove(ant1);
        verify(moveHelper).move(FieldPoint.point(9, 9), FieldPoint.point(10, 9));
        doMove(ant2);
        verify(moveHelper).move(FieldPoint.point(9, 10), FieldPoint.point(8, 10));
        doMove(ant1);
        verify(moveHelper).move(FieldPoint.point(10, 9), FieldPoint.point(11, 9));
        doMove(ant2);
        doMove(ant1);
        verify(moveHelper).move(FieldPoint.point(11, 9), FieldPoint.point(11, 10));
    }

    @Test
    public void kick() {
        when(view.isNear(any(FieldPoint.class))).thenReturn(false);
        Ant ant1 = new Ant(FieldPoint.point(8, 10), moveHelper, view);
        Ant ant2 = new Ant(FieldPoint.point(9, 10), moveHelper, view);
        ant2.doWalkToPoint(FieldPoint.point(12, 10));
        ant1.doWalkToPoint(FieldPoint.point(12, 10));
        syncField(ant1, ant2);
        assertEquals(FieldPoint.point(9, 10), ant1.getLocation());
        assertEquals(FieldPoint.point(10, 10), ant2.getLocation());
        when(moveHelper.canMoveTo(FieldPoint.point(10, 10))).thenReturn(false).thenReturn(true);
        when(moveHelper.kickOurAntAt(FieldPoint.point(10, 10))).thenReturn(MoveHelper.KickResult.KICKED_OUT);
        doMove(ant1);
        assertEquals(FieldPoint.point(10, 10), ant1.getLocation());
    }

    private void syncField(Ant... ants) {
        field.replaceAll(Item.ANT, Item.LAND);
        for (Ant ant: ants) {
            field.setItem(ant.getLocation(), Item.ANT);
        }
    }

    private void doMove(Ant... ants) {
        for (Ant ant: ants) {
            ant.beforeTurn();
            ant.update();
            syncField(ant);
        }
    }

}

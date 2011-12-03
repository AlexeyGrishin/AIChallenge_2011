package bot2.ai.distribution;

import bot2.ai.areas.distribution.AreaWalker;
import bot2.ai.areas.distribution.DistributableArea;
import bot2.ai.areas.distribution.Distributor;
import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class DistributorTestAbstract {

    private Distributor distributor;

    static interface MockableArea extends DistributableArea {
        int getPriority();
    }

    protected static final Integer DEFAULT = null;
    protected List<AreaWalker> createdWalkers = new ArrayList<AreaWalker>();
    protected List<DistributableArea> createdAreas = new ArrayList<DistributableArea>();

    @Before
    public void setUp() {
        createdWalkers.clear();
        createdAreas.clear();
        distributor = createDistributor();
    }

    protected AreaWalker createWalkerWhichStoreMoves(DistributableArea area, final Collection<DistributableArea> areasMovedTo) {
        AreaWalker walker = createWalker(area);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                areasMovedTo.add((DistributableArea) invocationOnMock.getArguments()[0]);
                return null;
            }
        }).when(walker).moveTo(any(DistributableArea.class));
        return walker;
    }

    protected AreaWalker createWalker(DistributableArea area) {
        AreaWalker walker = mock(AreaWalker.class);
        when(walker.getDestinationArea()).thenReturn(area);
        createdWalkers.add(walker);
        return walker;
    }

    protected AreaWalker createWalkerInMove(DistributableArea area) {
        AreaWalker walker = createWalker(area);
        when(walker.isInMove()).thenReturn(true);
        return walker;
    }

    protected DistributableArea createArea(String name, Integer requirements, int priority) {
        final MockableArea area = Mockito.mock(MockableArea.class, name);
        createdAreas.add(area);
        if (requirements != null) {
            when(area.getRequiredAmount(anyInt())).thenReturn(requirements);
        }
        else {
            when(area.getRequiredAmount(anyInt())).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    return invocationOnMock.getArguments()[0];
                }
            });
        }
        when(area.getPriority()).thenReturn(priority);
        when(area.compareTo(any(DistributableArea.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                MockableArea area2 = (MockableArea) invocationOnMock.getArguments()[0];
                return area2.getPriority() - area.getPriority();
            }
        });
        return area;
    }


    protected void doDistribute() {
        distributor.distribute(createdAreas, createdWalkers);
    }

    protected abstract Distributor createDistributor();

}

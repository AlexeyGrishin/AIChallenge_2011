package bot2.ai.distribution;

import bot2.ai.areas.distribution.AreaWalker;
import bot2.ai.areas.distribution.DistributableArea;
import bot2.ai.areas.distribution.Distributor;
import bot2.ai.areas.distribution.EvenDistributor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class EvenDistributorTest extends DistributorTestAbstract {

    @Override
    protected Distributor createDistributor() {
        return new EvenDistributor();
    }

    @Test
    public void twoAreas_singleAnt() {
        DistributableArea area1 = createArea("areaWithAnt", 1, 1);
        DistributableArea area2 = createArea("emptyArea", 1, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1));
        AreaWalker walker = createWalker(area1);

        doDistribute();
        verify(walker).moveTo(area2);
    }

    @Test
    public void twoAreas_singleAnt_inMove() {
        DistributableArea area1 = createArea("areaWithAnt", 1, 1);
        DistributableArea area2 = createArea("emptyArea", 1, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1));
        AreaWalker walker = createWalkerInMove(area1);

        doDistribute();
        verify(walker, never()).moveTo(area2);
    }

    @Test
    public void threeAreas_singleAnt_priority() {
        DistributableArea area1 = createArea("areaLowPriority", 1, 0);
        DistributableArea area2 = createArea("areaWithAnt", 1, 1);
        DistributableArea area3 = createArea("areaHighPriority", 1, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area3.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1, area3));
        AreaWalker walker = createWalker(area2);

        doDistribute();
        verify(walker).moveTo(area3);
        verify(walker, never()).moveTo(area1);
    }

    @Test
    public void fiveAreas_fiveAntsInSingleArea() {
        DistributableArea area1 = createArea("area1", 1, 1);
        DistributableArea area2 = createArea("area2", 1, 1);
        DistributableArea area3 = createArea("area3", 1, 1);
        DistributableArea area4 = createArea("area4", 1, 1);
        DistributableArea center = createArea("center", 1, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(center));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(center));
        when(area3.getNearestAreas()).thenReturn(Arrays.asList(center));
        when(area4.getNearestAreas()).thenReturn(Arrays.asList(center));
        when(center.getNearestAreas()).thenReturn(Arrays.asList(area1, area2, area3, area4));
        Set<DistributableArea> areasMovedTo = new HashSet<DistributableArea>();
        AreaWalker walker1 = createWalkerWhichStoreMoves(center, areasMovedTo);
        AreaWalker walker2 = createWalkerWhichStoreMoves(center, areasMovedTo);
        AreaWalker walker3 = createWalkerWhichStoreMoves(center, areasMovedTo);
        AreaWalker walker4 = createWalkerWhichStoreMoves(center, areasMovedTo);
        AreaWalker walker5 = createWalkerWhichStoreMoves(center, areasMovedTo);

        doDistribute();

        assertEquals(new HashSet<DistributableArea>(Arrays.asList(area1, area2, area3, area4)), areasMovedTo);

    }

    @Test
    public void threeAreas_twoAnts_inLine() {
        DistributableArea area1 = createArea("areaWithAnt-1", 1, 1);
        DistributableArea area2 = createArea("areaWithAnt-2", 1, 1);
        DistributableArea area3 = createArea("areaWithoutAnt", 1, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1, area3));
        when(area3.getNearestAreas()).thenReturn(Arrays.asList(area2));
        AreaWalker walker1 = createWalker(area1);
        AreaWalker walker2 = createWalker(area2);

        doDistribute();

        verify(walker2).moveTo(area3);
        verify(walker1).moveTo(area2);
    }


    @Test
    public void defaultDensity() {
        DistributableArea area1 = createArea("areaWithAnt-1", DEFAULT, 1);
        DistributableArea area2 = createArea("areaWithAnt-2", DEFAULT, 1);
        DistributableArea area3 = createArea("areaWithoutAnt-1", DEFAULT, 1);
        DistributableArea area4 = createArea("areaWithoutAnt-2", DEFAULT, 1);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1, area3));
        when(area3.getNearestAreas()).thenReturn(Arrays.asList(area2, area4));
        when(area4.getNearestAreas()).thenReturn(Arrays.asList(area3));
        List<DistributableArea> destinationFromArea1 = new ArrayList<DistributableArea>();
        List<DistributableArea> destinationFromArea4 = new ArrayList<DistributableArea>();
        AreaWalker walker1_1 = createWalkerWhichStoreMoves(area1, destinationFromArea1);
        AreaWalker walker1_2 = createWalkerWhichStoreMoves(area1, destinationFromArea1);
        AreaWalker walker1_3 = createWalkerWhichStoreMoves(area1, destinationFromArea1);
        AreaWalker walker1_4 = createWalkerWhichStoreMoves(area1, destinationFromArea1);
        AreaWalker walker2_1 = createWalkerWhichStoreMoves(area4, destinationFromArea4);
        AreaWalker walker2_2 = createWalkerWhichStoreMoves(area4, destinationFromArea4);
        AreaWalker walker2_3 = createWalkerWhichStoreMoves(area4, destinationFromArea4);
        AreaWalker walker2_4 = createWalkerWhichStoreMoves(area4, destinationFromArea4);
        doDistribute();

        assertEquals(Arrays.asList(area2, area2), destinationFromArea1);
        assertEquals(Arrays.asList(area3, area3), destinationFromArea4);

    }

    /*
    @Test
    public void walkToHighPriorityArea() {
        DistributableArea area1 = createArea("area-1", 1, 1);
        DistributableArea area2 = createArea("area-2", 1, 1);
        DistributableArea area3 = createArea("area-3", 1, 1);
        DistributableArea area4 = createArea("area-4", 100, 100);
        when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
        when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1, area3));
        when(area3.getNearestAreas()).thenReturn(Arrays.asList(area2, area4));
        when(area4.getNearestAreas()).thenReturn(Arrays.asList(area3));

        AreaWalker w1 = createWalker(area1);
        doDistribute();
        verify(w1).moveTo(area2);
        when(w1.getDestinationArea()).thenReturn(area2);
        doDistribute();
        verify(w1).moveTo(area3);
        when(w1.getDestinationArea()).thenReturn(area3);
        doDistribute();
        verify(w1).moveTo(area4);
    } */


}

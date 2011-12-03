package bot2.ai.distribution;

import bot2.ai.areas.distribution.AreaWalker;
import bot2.ai.areas.distribution.Assigner;
import bot2.ai.areas.distribution.DistributableArea;
import bot2.ai.areas.distribution.Distributor;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssignerTest extends DistributorTestAbstract {

    @Override
    protected Distributor createDistributor() {
        return new Assigner();
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
        verify(w1).moveTo(area4);
    }

}

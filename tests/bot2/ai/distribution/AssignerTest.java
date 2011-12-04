package bot2.ai.distribution;

import bot2.ai.areas.distribution.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import util.HasSize;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.HasSize.hasSize;

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

    public static class PriorityTest extends DistributorTestAbstract {

        private DistributableArea area1;
        private DistributableArea area2;
        private DistributableArea area3;
        private List<DistributableArea> assignedAreas;
        @Mock private NotDistributedHandler handler;

        @Override
        protected Distributor createDistributor() {
            Assigner assigner = new Assigner();
            assigner.setNotDistributedHandler(handler);
            return assigner;
        }

        @Before
        public void setUp() {
            super.setUp();
            area1 = createArea("area-1", 3, 10);
            area2 = createArea("area-2", 0, 0);
            area3 = createArea("area-3", 1, 1);
            when(area1.getNearestAreas()).thenReturn(Arrays.asList(area2));
            when(area2.getNearestAreas()).thenReturn(Arrays.asList(area1, area3));
            when(area3.getNearestAreas()).thenReturn(Arrays.asList(area2));
            assignedAreas = new ArrayList<DistributableArea>();
        }

        @Test
        public void distributeByRequirements_1() {
            createWalkersWhichStoreMoves(2, area2, assignedAreas);

            doDistribute();
            assertEquals(Arrays.asList(area1, area3), assignedAreas);
            verifyAllDistributed();
        }

        @Test
        public void distributeByRequirements_2() {
            createWalkersWhichStoreMoves(3, area2, assignedAreas);

            doDistribute();
            assertEquals(Arrays.asList(area1, area3, area1), assignedAreas);
            verifyAllDistributed();
        }

        @Test
        public void distributeByRequirements_3() {
            createWalkersWhichStoreMoves(4, area2, assignedAreas);

            doDistribute();
            assertEquals(Arrays.asList(area1, area3, area1, area1), assignedAreas);
            verifyAllDistributed();
        }

        @Test
        public void distributeByRequirements_overload() {
            createWalkersWhichStoreMoves(5, area2, assignedAreas);

            doDistribute();
            assertEquals(Arrays.asList(area1, area3, area1, area1), assignedAreas);
            verifyNotDistributed(1);
        }

        void verifyAllDistributed() {
            verify(handler, never()).distribute(anyCollectionOf(AreaWalker.class));
        }

        void verifyNotDistributed(int count) {
            verify(handler).distribute(argThat(new HasSize<Collection<AreaWalker>>(count)));
        }
    }

}

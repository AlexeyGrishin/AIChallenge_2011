package bot2.ai.areas.distribution;

import bot2.Logger;
import bot2.Time;
import bot2.ai.targets.Target;

import java.util.*;

public class Assigner implements Distributor {

    public final static int FIRST = 0;

    public static final NotDistributedHandler SEND_TO_NEAREST = new NotDistributedHandler() {
        public void distribute(Collection<AreaWalker> walkers) {
            for (AreaWalker walker : walkers) {
                List<DistributableArea> nearAreas = new ArrayList<DistributableArea>(walker.getLocation().getNearestAreas());
                if (!nearAreas.isEmpty()) {
                    Collections.sort(nearAreas);
                    walker.moveTo(nearAreas.get(FIRST));
                }
            }
        }
    };
    private NotDistributedHandler notDistributedHandler = SEND_TO_NEAREST;

    public void setNotDistributedHandler(NotDistributedHandler notDistributedHandler) {
        this.notDistributedHandler = notDistributedHandler;
    }

    public void distribute(Collection<DistributableArea> areas, Collection<AreaWalker> walkers) {
        Time.time.completed("Before distribution");
        List<AreaWalker> freeWalkers = new ArrayList<AreaWalker>(walkers.size());
        List<DistributableArea> alreadyServedAreas = new ArrayList<DistributableArea>(walkers.size());
        for (AreaWalker walker: walkers) {
            if (!walker.isInMove()) {
                freeWalkers.add(walker);
                alreadyServedAreas.add(walker.getLocation());
            }
            else {
                alreadyServedAreas.addAll(walker.getDestinationAreas());
            }
        }
        List<ReqArea> reqAreas = new LinkedList<ReqArea>();
        List<ReqArea> postponedReqAreas = new LinkedList<ReqArea>();
        for (DistributableArea area: areas) {
            ReqArea reqArea = new ReqArea(area, area.getRequiredAmount(1));
            while (alreadyServedAreas.contains(area)) {
                alreadyServedAreas.remove(area);
                reqArea.onWalkIn();
            }
            if (reqArea.requirements > 0) {
                reqAreas.add(reqArea);
            }
        }

        Collections.sort(reqAreas);

        while (!reqAreas.isEmpty() && !freeWalkers.isEmpty()) {
            List<ReqArea> nextSamePriority = getNextSamePriority(reqAreas);
            Logger.log(" Same priority areas: " + nextSamePriority);
            List<NearestWalker> walkersForThem = new ArrayList<NearestWalker>(reqAreas.size());
            for (ReqArea next: new ArrayList<ReqArea>(nextSamePriority)) {
                Logger.log("  Area " + next + " need to be visited by " + next.requirements + " ants");
                NearestWalker nearestWalker = findNearestWalker(freeWalkers, next);
                if (nearestWalker != null) {
                    walkersForThem.add(nearestWalker);
                }
                else {
                    Logger.log("   Cannot find any walker for area " + next.area);
                    nextSamePriority.remove(next);
                }
            }
            Collections.sort(walkersForThem);
            for (NearestWalker nearestWalker: walkersForThem) {
                AreaWalker walker = nearestWalker.walker;
                ReqArea target = nearestWalker.target;
                if (freeWalkers.contains(walker)) {
                    Logger.log("Send " + walker + " to " + target + ", distance = " + nearestWalker.distance);
                    walker.moveTo(target.area);
                    target.onWalkIn();
                    freeWalkers.remove(walker);
                    if (target.stillHasRequirements()) {
                        postponedReqAreas.add(target);
                    }
                }
                else {
                    Logger.log("Planned to send " + walker + " to " + target + ", distance = " + nearestWalker.distance + ", but it was already sent somewhere" );
                    postponedReqAreas.add(target);
                }

            }

            if (reqAreas.isEmpty()) {
                Logger.log("  Some areas require more ants, reiterate: " + postponedReqAreas);
                reqAreas.addAll(postponedReqAreas);
                postponedReqAreas.clear();
            }
        }
        if (!freeWalkers.isEmpty()) {
            Logger.log("  Some ants are without target, redistribute them");
            notDistributedHandler.distribute(freeWalkers);
        }


        Time.time.completed("After distribution");

    }

    private List<ReqArea> getNextSamePriority(List<ReqArea> reqAreas) {
        List<ReqArea> samePriority = new ArrayList<ReqArea>(reqAreas.size());
        ReqArea last = null;
        ReqArea next = reqAreas.get(FIRST);
        while (last == null || last.compareTo(next) == 0) {
            samePriority.add(next);
            reqAreas.remove(FIRST);
            if (reqAreas.isEmpty()) break;
            last = next;
            next = reqAreas.get(FIRST);
        }
        return samePriority;
    }

    private NearestWalker findNearestWalker(List<AreaWalker> freeWalkers, ReqArea reqArea) {
        Map<DistributableArea, AreaWalker> walkersAreas = new HashMap<DistributableArea, AreaWalker>();
        for (AreaWalker walker: freeWalkers) {
            walkersAreas.put(walker.getLocation(), walker);
        }
        Set<DistributableArea> visited = new HashSet<DistributableArea>();
        List<DistributableArea> toProcess = new LinkedList<DistributableArea>();
        List<Integer> toProcessDistance = new LinkedList<Integer>();
        toProcess.add(reqArea.area);
        toProcessDistance.add(0);
        while (!toProcess.isEmpty()) {
            DistributableArea area = toProcess.remove(0);
            if (visited.contains(area)) {
                continue;
            }
            int distance = toProcessDistance.remove(0);
            AreaWalker walker = walkersAreas.get(area);
            if (walker != null) {
                return new NearestWalker(walker, distance, reqArea);
            }
            visited.add(area);
            for (DistributableArea narea: area.getNearestAreas()) {
                if (!visited.contains(narea)) {
                    toProcess.add(narea);
                    toProcessDistance.add(distance + 1);
                }
            }
        }
        return null;
    }

    class NearestWalker implements Comparable<NearestWalker> {
        public final AreaWalker walker;
        public final int distance;
        public final ReqArea target;

        NearestWalker(AreaWalker walker, int distance, ReqArea target) {
            this.walker = walker;
            this.distance = distance;
            this.target = target;
        }

        public int compareTo(NearestWalker o) {
            return distance - o.distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NearestWalker that = (NearestWalker) o;

            if (!walker.equals(that.walker)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return walker.hashCode();
        }
    }

    class ReqArea implements Comparable<ReqArea> {
        final DistributableArea area;
        private int requirements;

        ReqArea(DistributableArea area, int requirements) {
            this.area = area;
            this.requirements = requirements;
        }

        public int compareTo(ReqArea o) {
            return area.compareTo(o.area);
        }

        public void onWalkIn() {
            requirements--;
        }

        public boolean stillHasRequirements() {
            return requirements > 0;
        }

        public String toString() {
            return area + "(requires " + requirements + ")";
        }

    }
}

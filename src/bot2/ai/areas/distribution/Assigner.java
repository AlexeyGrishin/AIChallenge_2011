package bot2.ai.areas.distribution;

import bot2.Logger;
import bot2.Time;

import java.util.*;

public class Assigner implements Distributor {

    /*
    1. сейчас если 3 муравья и 1 высокоприоритетная точка, то ход за ходом к ней отправятся все трое

       надо для муравья хранить два участка - итоговый, и следующий

       если он движется, то учитывать итоговый. если покоится - то следующий.

    2.


     */
    public void distribute(Collection<DistributableArea> areas, Collection<AreaWalker> walkers) {
        Time.time.completed("Before distribution");
        List<AreaWalker> freeWalkers = new ArrayList<AreaWalker>(walkers.size());
        List<DistributableArea> alreadyServedAreas = new ArrayList<DistributableArea>(walkers.size());
        for (AreaWalker walker: walkers) {
            if (!walker.isInMove()) {
                freeWalkers.add(walker);
            }
            alreadyServedAreas.add(walker.getDestinationArea());
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
                Logger.log("  Area " + area + " need to be visited by " + reqArea.requirements + " ants");
            }
        }

        Collections.sort(reqAreas);

        while (!reqAreas.isEmpty() && !freeWalkers.isEmpty()) {
            ReqArea next = reqAreas.remove(0);
            AreaWalker nearestWalker = findNearestWalker(freeWalkers, next);
            if (nearestWalker != null) {
                nearestWalker.moveTo(next.area);
                freeWalkers.remove(nearestWalker);
                next.onWalkIn();
                if (next.stillHasRequirements()) {
                    postponedReqAreas.add(next);
                }

            }
            else {
                Logger.log("Cannot find any walker for area " + next.area + ", probably they aready on it: " + freeWalkers);
            }
            if (reqAreas.isEmpty()) {
                Logger.log("  Some areas require more ants, reiterate: " + postponedReqAreas);
                reqAreas.addAll(postponedReqAreas);
                postponedReqAreas.clear();
            }
        }
        if (!freeWalkers.isEmpty()) {
            Logger.log("  Some ants are without target, send them to nearest areas");
        }
        for (AreaWalker walker: freeWalkers) {
            List<DistributableArea> nearAreas = new ArrayList<DistributableArea>(walker.getDestinationArea().getNearestAreas());
            Collections.sort(nearAreas);
            walker.moveTo(nearAreas.get(0));
        }

        Time.time.completed("After distribution");

    }

    private AreaWalker findNearestWalker(List<AreaWalker> freeWalkers, ReqArea reqArea) {
        Map<DistributableArea, AreaWalker> walkersAreas = new HashMap<DistributableArea, AreaWalker>();
        for (AreaWalker walker: freeWalkers) {
            walkersAreas.put(walker.getDestinationArea(), walker);
        }
        Set<DistributableArea> visited = new HashSet<DistributableArea>();
        List<DistributableArea> toProcess = new LinkedList<DistributableArea>();
        toProcess.add(reqArea.area);
        while (!toProcess.isEmpty()) {
            DistributableArea area = toProcess.remove(0);
            AreaWalker walker = walkersAreas.get(area);
            if (walker != null) {
                return walker;
            }
            visited.add(area);
            for (DistributableArea narea: area.getNearestAreas()) {
                if (!visited.contains(narea)) {
                    toProcess.add(narea);
                }
            }
        }
        return null;
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
    }
}

package bot2.ai.areas.distribution;

import java.util.*;

//!not synchronized!
public class EvenDistributor implements Distributor {


    private Map<DistributableArea, PrioritizedArea> priorAreas;

    public void distribute(Collection<DistributableArea> areas, Collection<AreaWalker> walkers) {
        double density = (double)walkers.size() / areas.size();
        int defaultDensity = (int)Math.ceil(density);

        priorAreas = new HashMap<DistributableArea, PrioritizedArea>();

        for (DistributableArea area: areas) {
            PrioritizedArea parea = new PrioritizedArea(area, area.getRequiredAmount(defaultDensity));
            priorAreas.put(area, parea);
        }

        for (AreaWalker walker: walkers) {
            for (DistributableArea area: walker.getDestinationAreas()) {
                PrioritizedArea parea = priorAreas.get(area);
                parea.addWalker(walker);
            }
        }

        for (PrioritizedArea parea: priorAreas.values()) {
            parea.calculatePriority();
        }
        List<PrioritizedArea> sortedList = new LinkedList<PrioritizedArea>(priorAreas.values());
        sortAreas(sortedList);

        while (!sortedList.isEmpty()) {
            PrioritizedArea nextArea = sortedList.remove(0);
            int req = nextArea.requirement;
            if (req < 0) {
                break;
            }
            Collection<PrioritizedArea> nearAreas = nextArea.getNearestAreas();
            for (PrioritizedArea area: nearAreas) {
                int freeWalkers = area.getFreeWalkers();
                for (int i = 0; i < freeWalkers && i < req; i++) {
                    area.sendWalker(nextArea);
                }
                if (nextArea.requirement == 0) {
                    break;
                }
            }
            sortAreas(sortedList);
        }
    }

    private void sortAreas(List<PrioritizedArea> sortedList) {
        Collections.sort(sortedList, new Comparator<PrioritizedArea>() {
            public int compare(PrioritizedArea o1, PrioritizedArea o2) {
                int res = -(o1.requirement - o2.requirement);
                if (res == 0) {
                    return o1.area.compareTo(o2.area);
                }
                return res;
            }
        });
    }


    class PrioritizedArea {
        DistributableArea area;
        private int amountOfObjects;
        private int amountOfRequiredObjects;
        private List<AreaWalker> freeWalkers = new ArrayList<AreaWalker>();

        int requirement = 0;
        private ArrayList<PrioritizedArea> nearAreas;


        PrioritizedArea(DistributableArea area, int amountOfRequiredObjects) {
            this.area = area;
            amountOfObjects = 0;
            this.amountOfRequiredObjects = amountOfRequiredObjects;
        }

        public void addWalker(AreaWalker walker) {
            amountOfObjects++;
            if (!walker.isInMove()) {
                freeWalkers.add(walker);
            }
        }

        public void calculatePriority() {
            requirement = amountOfRequiredObjects - amountOfObjects;
        }

        public int getRequiredAmount() {
            return requirement > 0 ? requirement : 0;
        }

        public void sendWalker(PrioritizedArea anotherArea) {
            freeWalkers.remove(0).moveTo(anotherArea.area);
            anotherArea.onWalkIn();
            amountOfObjects--;
            calculatePriority();
        }

        public int getFreeWalkers() {
            return freeWalkers.size();
        }

        public void onWalkIn() {
            amountOfObjects++;
            calculatePriority();
        }

        public Collection<PrioritizedArea> getNearestAreas() {
            if (nearAreas == null) {
                nearAreas = new ArrayList<PrioritizedArea>(4);
                for (DistributableArea narea: area.getNearestAreas()) {
                    nearAreas.add(priorAreas.get(narea));
                }
            }
            return nearAreas;
        }
    }



}

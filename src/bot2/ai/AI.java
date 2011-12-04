package bot2.ai;

import bot2.GameSettings;
import bot2.Logger;
import bot2.ai.areas.Areas;
import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;
import bot2.ai.areas.distribution.*;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;

import java.util.*;

public class AI implements GameStrategy {

    private Assigner areasDistributor = new Assigner();
    private BetweenTargetsDistributor<AreaWalker, FieldArea> antByAreasDistributor = new BetweenTargetsDistributor<AreaWalker, FieldArea>();
    private GameSettings settings;

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public void doTurn(List<Ant> ants, Field field, Areas areas, Hills hills) {
        List<Ant> freeAnts = new ArrayList<Ant>(ants);
        doAttackVisibleHills(freeAnts, hills, field);
        doGatherFood(freeAnts, field);
        doInspectNewArea(freeAnts, field, areas);
        for (Ant ant: ants) {
            ant.update();
        }
    }

    private void doAttackVisibleHills(List<Ant> freeAnts, Hills hills, Field field) {
        if (hills.getEnemiesHills().isEmpty()) return;
        for (Ant ant: new ArrayList<Ant>(freeAnts)) {
            if (ant.isAttackingHill()) {
                freeAnts.remove(ant);
            }
            else {
                for (FieldPoint enemyHill: hills.getEnemiesHills()) {
                    if (ant.getVisibleView().canReach(enemyHill)) {
                        ant.doAttackHill(enemyHill, field);
                        freeAnts.remove(ant);
                    }
                }
            }
        }
    }

    private void doInspectNewArea(List<Ant> freeAnts, Field field, final Areas areas) {
        final AreaWrapper<FieldArea> wrapper = new RankedFieldAreaWrapper(new AreasPathHelper(field));
        List<DistributableArea> distributableAreas = new ArrayList<DistributableArea>(areas.getCount());
        for (FieldArea area: areas.getAllAreas()) {
            if (area == null) break;
            distributableAreas.add(wrapper.wrap(area));
        }
        List<AreaWalker> walkers = new ArrayList<AreaWalker>(freeAnts.size());
        for (Ant ant: freeAnts) {
            FieldArea antsArea = getAntArea(ant, areas);
            if (antsArea != null) {
                walkers.add(new AntAreaWalker(ant, antsArea, wrapper));
            }
            else {
                FieldArea targetArea = areas.getNearestUnknownArea(ant.getLocation());
                if (targetArea == null) {
                    targetArea = areas.getNearestArea(ant.getLocation());
                }
                ant.doReachArea(targetArea);
            }
        }
        areasDistributor.setNotDistributedHandler(createNotDistributedHandler(areas, wrapper));
        areasDistributor.distribute(distributableAreas, walkers);

    }

    private NotDistributedHandler createNotDistributedHandler(final Areas areas, final AreaWrapper<FieldArea> wrapper) {
        return new NotDistributedHandler() {
            public void distribute(Collection<AreaWalker> walkers) {
                List<FieldArea> notReached = getNotReachedAreas(areas);
                List<AreaWalker> freeWalkers = new ArrayList<AreaWalker>(walkers);
                if (!notReached.isEmpty()) {
                    Logger.log("Send not distributed ants to unreached areas");
                    freeWalkers.clear();
                    //TODO: тут надо считать дистанцию от текущего местоположения, а не от цели
                    for (BetweenTargetsDistributor.SourceTarget<AreaWalker, FieldArea> st: antByAreasDistributor.distribute(walkers, notReached, createMeasurer(areas, wrapper))) {
                        if (st.target != null)
                            st.source.moveTo(wrapper.wrap(st.target));
                        else
                            freeWalkers.add(st.source);
                    }
                }
                if (!freeWalkers.isEmpty()) {
                    Logger.log("Send not distributed ants to near areas - no unreached");
                    Assigner.SEND_TO_NEAREST.distribute(walkers);
                }
            }
        };
    }

    private List<FieldArea> getNotReachedAreas(Areas areas) {
        List<FieldArea> notReached = new ArrayList<FieldArea>();
        for (FieldArea area: areas.getAllAreas()) {
            if (area == null) break;
            if (!area.isReached()) {
                notReached.add(area);
            }
        }
        return notReached;
    }

    private BetweenTargetsDistributor.DistanceMeasurer<AreaWalker, FieldArea> createMeasurer(final Areas areas, final AreaWrapper<FieldArea> wrapper) {
        return new BetweenTargetsDistributor.DistanceMeasurer<AreaWalker, FieldArea>() {
            public int measureDistance(AreaWalker from, FieldArea to) {
                FieldArea sourceArea = wrapper.unwrap(from.getDestinationArea());
                if (sourceArea == to) return 0;
                int distance = areas.findPath(sourceArea, to).size();
                if (distance == 0) {
                    return NOT_FOUND;
                }
                return distance;
            }
        };
    }

    private FieldArea getAntArea(Ant ant, Areas areas) {
        FieldArea area = ant.getTargetArea();
        if (area == null) {
            area = areas.get(ant.getTargetPoint());
        }
        return area;
    }

    //TODO: remove
    private void doInspectNewArea_old(List<Ant> freeAnts, Field field, Areas areas) {
        List<FieldArea> willBeInspected = new ArrayList<FieldArea>();
        for (Ant ant: new ArrayList<Ant>(freeAnts)) {
            if (ant.isBusy()) {
                freeAnts.remove(ant);
                willBeInspected.add(ant.getTargetArea());
            }
        }
        for (Ant ant: freeAnts) {
            FieldArea area = areas.get(ant.getLocation());
            List<FieldArea> nearestAreas;
            FieldArea selected = null;
            if (area != null) {
                nearestAreas = new ArrayList<FieldArea>(area.getNearAreasCollection());
                if (!willBeInspected.containsAll(nearestAreas))
                    nearestAreas.removeAll(willBeInspected);
                Collections.sort(nearestAreas, new Comparator<FieldArea>() {
                    public int compare(FieldArea o1, FieldArea o2) {
                        if (o2.isReached() && !o1.isReached())
                            return -1;
                        if (!o2.isReached() && o1.isReached())
                            return 1;
                        /*int res = o2.getDistanceToHill() - o1.getDistanceToHill();
                        if (res == 0)
                            res = -(o1.getStat().getAlies() - o2.getStat().getAlies());*/
                        /*return res;*/
                        int res = -(o1.getStat().getVisitedTurnsAgo()*o1.getStat().getOpened() - o2.getStat().getVisitedTurnsAgo()*o2.getStat().getOpened());
                        if (res == 0) {
                            res = o1.getStat().getOpened() - o2.getStat().getOpened();
                        }
                        /*if (res == 0) {
                            res = -(o1.getStat().getAlies() - o2.getStat().getAlies());
                        } */
                        if (res == 0) {
                            res = o2.getDistanceToHill() - o1.getDistanceToHill();
                        }
                        return res;
                    }
                });
                logAreasToSelect(ant, nearestAreas);
                if (!nearestAreas.isEmpty()) {
                    selected = nearestAreas.get(0);
                }
                else {
                    Logger.log("WARN: " + ant + " was unable to find near areas for " + area);
                }
            }
            else {
                selected = areas.getNearestUnknownArea(ant.getLocation());
            }

            if (selected != null) {
                ant.doReachArea(selected);
                willBeInspected.add(selected);
            }
            else {
                Logger.log("WARN: " + ant + " was unabled to find the area to reach O_o");
            }

        }
    }

    private void logAreasToSelect(Ant ant, List<FieldArea> nearestAreas) {
        Logger.log(ant + ": select next areas to go: " + nearestAreas);
        for (FieldArea area: nearestAreas) {
            Logger.log(area + ": " + area.getStat());
        }
    }

    private void doGatherFood(List<Ant> freeAnts, Field field) {
        List<FieldPoint> assignedFood = new ArrayList<FieldPoint>();
        for (Ant ant: new ArrayList<Ant>(freeAnts)) {
            if (ant.isBusy() && ant.isHarvesting()) {
                freeAnts.remove(ant);
                assignedFood.add(ant.getTargetPoint());
            }
        }
        for (Ant ant: new ArrayList<Ant>(freeAnts)) {
            List<FieldPoint> food = ant.getVisibleView().getItems(Item.FOOD);
            if (!food.isEmpty()) {
                FieldPoint nearestFood = null;
                int nearestDistance = 99999;
                for (FieldPoint foodPoint: food) {
                    if (!assignedFood.contains(foodPoint)) {
                        int distance = field.getQuickDistance(foodPoint, ant.getLocation());
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestFood = foodPoint;
                        }
                    }
                }
                if (nearestFood != null) {
                    ant.doGatherFood(nearestFood);
                    freeAnts.remove(ant);
                    assignedFood.add(nearestFood);
                }
            }
        }
    }

    public boolean shallRevisit(FieldArea area) {
        return area.getStat().getVisitedTurnsAgo() > settings.getViewRadius() * 4;
    }
}

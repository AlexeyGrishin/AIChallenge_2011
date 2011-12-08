package bot2.ai;

import bot2.GameSettings;
import bot2.Logger;
import bot2.ai.areas.Areas;
import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;
import bot2.ai.areas.distribution.*;
import bot2.ai.battle.Battle;
import bot2.ai.battle.BattleCase;
import bot2.ai.battle.BattleCaseResolution;
import bot2.ai.battle.BattleStrategy;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;

import java.beans.beancontext.BeanContext;
import java.util.*;

import static bot2.ai.battle.BattleStrategy.maxDamage;
import static bot2.ai.battle.BattleStrategy.minLost;

public class AI implements GameStrategy {

    private Assigner areasDistributor = new Assigner();
    private Assigner areasDistributorUnreached = new Assigner();
    private BetweenTargetsDistributor<AreaWalker, FieldArea> antByAreasDistributor = new BetweenTargetsDistributor<AreaWalker, FieldArea>();
    private GameSettings settings;
    private int visitDivider;
    private Battle battle;

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public void doTurn(List<Ant> ants, Field field, Areas areas, Hills hills) {
        List<Ant> freeAnts = new ArrayList<Ant>(ants);
        doBattle(field, freeAnts);
        doAttackVisibleHills(freeAnts, hills, field);
        doGatherFood(freeAnts, field);
        doInspectNewArea(freeAnts, field, areas);
        for (Ant ant: ants) {
            ant.update();
        }
    }

    private void doBattle(Field field, List<Ant> ants) {
        if (battle == null) {
            battle = new Battle(settings, field, this);
        }
        battle.process(ants);
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
            AntAreaWalker walker = new AntAreaWalker(ant, wrapper, areas);
            if (!walker.isInMove()) {
                if (walker.getLocation() != null) {
                    walkers.add(walker);
                }
                else {
                    FieldArea targetArea = areas.getNearestUnknownArea(ant);
                    if (targetArea == null) {
                        targetArea = areas.getNearestArea(ant);
                    }
                    ant.doExitUnknownArea(targetArea, areas);
                }
            }
            else {
                //it will be used to filter areas which will be visited soon
                walkers.add(walker);
            }
        }
        areasDistributor.setNotDistributedHandler(createNotDistributedHandler(areas, field));
        areasDistributor.distribute(distributableAreas, walkers);

    }

    private NotDistributedHandler createNotDistributedHandler(final Areas areas, final Field field) {
        return new NotDistributedHandler() {
            public void distribute(Collection<AreaWalker> walkers) {
                OnlyUnreachedRankedFieldAreaWrapper wrapper = new OnlyUnreachedRankedFieldAreaWrapper(
                        new AreasPathHelper(field)
                );
                Collection<DistributableArea> notReached = wrapper.wrap(getNotReachedAreas(areas));//new ArrayList<DistributableArea>(areas.getCount());
                /*boolean existingUnreached = false;
                for (FieldArea area: areas.getAllAreas()) {
                    if (area == null) break;
                    if (!area.isReached()) {
                        existingUnreached = true;
                    }
                    notReached.add(wrapper.wrap(area));
                } */
                if (!notReached.isEmpty()) {
                    Logger.log("Send not distributed ants to unreached areas");
                    //TODO: ugly
                    for (AreaWalker walker: walkers) {
                        ((AntAreaWalker)walker).setWrapper(wrapper);
                    }
                    areasDistributorUnreached.distribute(notReached, walkers);
                }
                else {
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
                FieldArea sourceArea = wrapper.unwrap(from.getLocation());
                if (sourceArea == to) return 0;
                int distance = areas.findPath(sourceArea, to).size();
                if (distance == 0) {
                    return NOT_FOUND;
                }
                return distance;
            }
        };
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
        visitDivider = settings.getViewRadius() * 4;
        return area.getStat().getVisitedTurnsAgo() > visitDivider;
    }

    public int getVisitRank(int visitedAgo) {
        return visitedAgo / visitDivider;
    }

    public BattleCase<FieldPoint> select(List<BattleCase<FieldPoint>> cases, List<FieldPoint> ours, List<FieldPoint> enemies) {
        Logger.log("Resolute battle");
        Logger.log("  Ours: " + ours);
        Logger.log("  Enemies: " + enemies);
        BattleCase<FieldPoint> res = resolute(cases, ours, enemies);
        Logger.log("  -> " + res);
        return res;
    }

    private BattleCase<FieldPoint> resolute(List<BattleCase<FieldPoint>> cases, List<FieldPoint> ours, List<FieldPoint> enemies) {
        if (ours.size() > enemies.size()) {
            return addFlag(maxDamage(cases), "Attack");
        }
        else {
            return minLost(cases);
        }
    }

    private BattleCase<FieldPoint> addFlag(BattleCase<FieldPoint> c, String flag) {
        c.flag = flag;
        return c;
    }
}

package bot.brain;

import bot.*;
import bot.brain.teams.Guard;
import pathfinder.AntsHelper;
import pathfinder.PathFinder;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class BrainBot extends Bot {


    private static final int ME = 0;
    private static final int AUTO_RUSH_DISTANCE = 9;
    private Strategy strategy;
    private boolean firstRun = true;
    private List<Guard> guards = new ArrayList<Guard>();
    private List<Ant> justBorn = new ArrayList<Ant>();
    private TimeGuard guard;
    private String logPath;

    public BrainBot(Strategy strategy, String logPath) {
        this.strategy = strategy;
        this.logPath = logPath;
    }

    public BrainBot(Strategy strategy) {
        this.strategy = strategy;
        this.logPath = null;
    }

    @Override
    protected String getLogPath() {
        return logPath;
    }

    @Override
    public void beforeUpdate() {
        justBorn.clear();
        oldAnts.clear();
        oldAnts.addAll(ants);
        ants.clear();
        super.beforeUpdate();
    }

    @Override
    public void afterUpdate() {
        try {
            for (Ant ant: oldAnts) {
                ant.die();
            }
            if (firstRun) {
                for (Tile hill: getAnts().getMyHills()) {
                    Guard guard = new Guard(hill);
                    guards.add(guard);
                    guard.setField(getAnts());
                }
                firstRun = false;
            }
        }
        catch (Throwable e) {
            getAnts().log("Exception in afterUpdate: " + e.toString());
            for (StackTraceElement el: e.getStackTrace()) {
                getAnts().log("    " + el.toString());
            }
        }
    }

    private long logTiming(long from, String action) {
        long ts = System.currentTimeMillis();
        getAnts().log("     " + action + ": " + (ts - from) + ", " + getAnts().getTimeRemaining() + " left");
        return ts;
    }

    @Override
    public void doTurn() {
        try {
            Ants ants = getAnts();
            initTimeGuard(ants);
            ants.log("-> Turn " + ants.getTurn());
            long ts = System.currentTimeMillis();
            List<Tile> unorderedFood = new ArrayList<Tile>(ants.getFoodTiles());
            for (Ant ant: this.ants) {
                ant.onNewTurn();
                ant.update();   //do a step for each
            }
            removeCrushedHills(ants);
            //then observe those who are not busy and assign orders
            ts = logTiming(ts, "Update1");
            List<Ant> freeAnts = new ArrayList<Ant>(this.ants);
            guardOurHill(freeAnts);
            ts = logTiming(ts, "Guard");
            rushTheHill(freeAnts);
            ts = logTiming(ts, "Rush");
            harvestFood(freeAnts, unorderedFood);
            ts = logTiming(ts, "Harvest");
            inspectNewArea(freeAnts);
            ts = logTiming(ts, "New area");

            //and update those of them who did not performed an action before
            for (Ant ant: this.ants) {
                ant.update();   //do a step for each
            }
            ts = logTiming(ts, "Update2");
            if (ants.getTimeRemaining() < ants.getTurnTime() / 2) {
                ants.log("WARNING: only " + ants.getTimeRemaining() + " remains from " + ants.getTurnTime());
            }
        }
        //catch (TimeoutException e) {
        //    getAnts().log("Problem: timeout =(");
        //}
        catch (Throwable e) {
            getAnts().log("Exception: " + e.toString());
            for (StackTraceElement el: e.getStackTrace()) {
                getAnts().log("    " + el.toString());
            }
        }
    }

    private void initTimeGuard(Ants ants) {
        if (guard == null) {
            guard = new TimeGuard(ants);
        }
    }

    private void guardOurHill(List<Ant> ants) {
        int guardCount = 0;
        int hillsInDanger = 0;
        for (Guard g: guards) {
            guardCount += g.getCount();
            if (g.isHillInDanger()) {
                hillsInDanger +=1;
            }
        }
        int newCount = strategy.getCountOfGuards(ants, hillsInDanger) - guardCount;
        getAnts().log("Count of ants: " + ants.size() + ", count of guards right now: " + guardCount + ", count of new guards: " + newCount + ", count of hills in danger: " + hillsInDanger);
        if (newCount > 0) {
            int assignedCount = 0;
            for (Ant ant: ants) {
                for (Guard guard: guards) {
                    boolean shallAssign = guard.isNear(ant);
                    if (guard.isRequired() && guard.isHillAlive() && shallAssign) {
                        getAnts().log("New guard: " + ant);
                        guard.assign(ant);
                        assignedCount++;
                        break;
                    }
                }
                if (assignedCount >= newCount) break;
            }
        }
        for (Guard g: guards) {
            g.doTurn();
            g.removeManagedFrom(ants);
        }
    }

    private void removeCrushedHills(Ants ants) {
        for (Tile hill: new ArrayList<Tile>(ants.getEnemyHills())) {
            if (ants.getIlk(hill) == Ilk.MY_ANT) {
                ants.removeEnemyHill(hill);
                reachableEnemyHills.remove(hill);
            }
        }
    }

    private Set<Tile> reachableEnemyHills = new HashSet<Tile>();

    private void updateReachableEnemyHills() {
        Set<Tile> stillUnreachedEnemyHills = new HashSet<Tile>(getAnts().getEnemyHills());
        stillUnreachedEnemyHills.removeAll(reachableEnemyHills);
        for (Tile hill: stillUnreachedEnemyHills) {
            for (Ant ant: ants) {
                if (ant.see(hill)) {
                    if (getAnts().canReach(ant, hill)) {
                        reachableEnemyHills.add(hill);
                        getAnts().log("Found reachable hill: " + hill);
                        break;
                    }
                }
            }
        }
        stillUnreachedEnemyHills.removeAll(reachableEnemyHills);
        if (!stillUnreachedEnemyHills.isEmpty())
            getAnts().log("The following hills were found, but not reachable: " + stillUnreachedEnemyHills);
    }



    private void rushTheHill(List<Ant> ants) {
        //TODO: it does not know when the hill is destroyed
        updateReachableEnemyHills();
        Ants ants1 = getAnts();
        if (!reachableEnemyHills.isEmpty()) {
            Tile hill = reachableEnemyHills.iterator().next();
            int newRushers = strategy.getCountOfRushers(ants);
            final List<Ant> canAttackTheHill = new ArrayList<Ant>();
            final List<Integer> distances = new ArrayList<Integer>();
            for (Ant ant: ants) {
                int distance = ants1.getDistance(ant.getPosition(), hill);
                if (newRushers > 0 && !ant.isHarvesting()) {
                    canAttackTheHill.add(ant);
                    distances.add(distance);
                }
                if (newRushers == 0 && distance <= AUTO_RUSH_DISTANCE) {
                    ant.doAttackTheHill(hill);
                }
                if (newRushers < 0 && ant.isRushing()) {
                    ant.done();
                    newRushers++;
                }
                if (newRushers == 0) break;
            }

            Collections.sort(canAttackTheHill, new Comparator<Ant>() {
                public int compare(Ant o1, Ant o2) {
                    return distances.get(canAttackTheHill.indexOf(o1)).compareTo(
                            distances.get(canAttackTheHill.indexOf(o2))
                    );
                }
            });
            int failuresCount = 0;
            for (int i = 0; i < canAttackTheHill.size() && i < newRushers; i++) {
                if (!canAttackTheHill.get(i).doAttackTheHill(hill)) {
                    failuresCount++;
                }
            }
            if (failuresCount > 10) {
                ants1.log("Seems like hill at " + hill + " is not reachable yet =(");
                reachableEnemyHills.remove(hill);
            }
        }
    }

    private void inspectNewArea(List<Ant> freeAnts) {
        for (Ant ant: freeAnts) {
            if (ant.isBusy()) continue;
            Tile position = ant.getPosition();
            int dx = 0, dy = 0;
            int distance = new Random().nextInt(10) + 3;
            switch (Aim.values()[new Random().nextInt(4)]) {
                case NORTH: dy = +distance; break;
                case EAST: dx = +distance; break;
                case WEST: dx = -distance; break;
                case SOUTH: dy = -distance; break;
            }
            Tile tile = getAnts().normalize(position.getRow() + dy, position.getCol() + dx);
            ant.goAndSeeAround(tile);
        }

    }

    //for tests only. Do not use it in game!
    public boolean isGuard(Ant ant) {
        List<Ant> ants = new ArrayList<Ant>(this.ants);
        for (Guard g: guards) {
            g.removeManagedFrom(ants);
        }
        return !ants.contains(ant);
    }

    class AntFoodDistance implements Comparable<AntFoodDistance> {
        Ant ant;
        List<Tile> food = new ArrayList<Tile>(6);
        List<Integer> distances = new ArrayList<Integer>(6);
        int maxDistance = 0;
        int minDistance = 9999999;

        AntFoodDistance(Ant ant) {
            this.ant = ant;
        }

        void registerFood(Tile food) {
            int distance = ant.getDistanceToVisibleObject(food);
            if (distance >= 0) {
                this.food.add(food);
                this.distances.add(distance);
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }

        void deregisterFood(Tile food) {
            int index = this.food.indexOf(food);
            if (index >= 0) {
                this.food.remove(index);
                int distance = this.distances.remove(index);
                if (!this.distances.isEmpty() && distance == minDistance) {
                    minDistance = Collections.min(distances);
                }
            }
        }

        public int compareTo(AntFoodDistance o) {
            return o.maxDistance - maxDistance;
        }

        Tile issueOrder() {
            if (!this.food.isEmpty()) {
                int index = distances.indexOf(minDistance);
                Tile tile = food.get(index);
                if (ant.harvestFood(tile)) {
                    return tile;
                }
            }
            return null;
        }
    }

    private void harvestFood(List<Ant> ants, List<Tile> unorderedFood) {
        List<AntFoodDistance> distances = new LinkedList<AntFoodDistance>();
        for (Ant ant: ants) {
            if (ant.isRushing()) {
                //just ignore
            } else if (ant.isHarvesting() /*&& is harverst*/) {
                unorderedFood.remove(ant.getTarget());
            }
            else {
                AntFoodDistance adf = new AntFoodDistance(ant);
                distances.add(adf);
                for (Tile food: unorderedFood) {
                    adf.registerFood(food);
                }
            }
        }
        Collections.sort(distances);
        List<Tile> orderedFood = new ArrayList<Tile>(unorderedFood.size());
        while (!distances.isEmpty()) {
            AntFoodDistance d = distances.remove(0);
            for (Tile food: orderedFood) {
                d.deregisterFood(food);
            }
            Tile markedFood = d.issueOrder();
            if (markedFood != null) {
                orderedFood.add(markedFood);
            }
        }
    }

    private List<Ant> ants = new ArrayList<Ant>();
    private List<Ant> oldAnts = new ArrayList<Ant>();

    @Override
    public Tile addAnt(int row, int col, int owner) {
        Tile tile = super.addAnt(row, col, owner);
        if (owner == ME) {
            boolean found = false;
            for (Ant ant: oldAnts) {
                if (ant.isOn(tile)) {
                    this.ants.add(ant);
                    this.oldAnts.remove(ant);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Ant ant = new Ant(tile, getAnts());
                this.ants.add(ant);
                this.justBorn.add(ant);
            }
        }
        return tile;
    }


    public List<Ant> getTheAnts() {
        return ants;
    }

}

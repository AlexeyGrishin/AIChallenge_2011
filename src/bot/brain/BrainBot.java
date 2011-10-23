package bot.brain;

import bot.*;
import bot.brain.teams.Guard;

import java.util.*;

public class BrainBot extends Bot {


    private static final int ME = 0;
    private Strategy strategy;
    private boolean firstRun = true;
    private List<Guard> guards = new ArrayList<Guard>();
    private List<Ant> justBorn = new ArrayList<Ant>();

    public BrainBot(Strategy strategy) {
        this.strategy = strategy;
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

    private long logTiming(long from, String action) {
        long ts = System.currentTimeMillis();
        getAnts().log("     " + action + ": " + (ts - from) + ", " + getAnts().getTimeRemaining() + " left");
        return ts;
    }

    @Override
    public void doTurn() {
        try {
            Ants ants = getAnts();
            ants.log("-> Turn");
            List<Tile> unorderedFood = new ArrayList<Tile>(ants.getFoodTiles());
            for (Ant ant: this.ants) {
                ant.onNewTurn();
                ant.update();   //do a step for each
            }
            removeCrushedHills(ants);

            //then observe those who are not busy and assign orders
            List<Ant> freeAnts = new ArrayList<Ant>(this.ants);
            guardOurHill(freeAnts);
            rushTheHill(freeAnts);
            harvestFood(freeAnts, unorderedFood);
            inspectNewArea(freeAnts);

            //and update those of them who did not performed an action before
            for (Ant ant: this.ants) {
                ant.update();   //do a step for each
            }
            if (ants.getTimeRemaining() < ants.getTurnTime() / 2) {
                ants.log("WARNING: only " + ants.getTimeRemaining() + " remains from " + ants.getTurnTime());
            }
        }
        catch (Throwable e) {
            getAnts().log("Exception: " + e.toString());
            for (StackTraceElement el: e.getStackTrace()) {
                getAnts().log("    " + el.toString());
            }
        }
    }

    private void guardOurHill(List<Ant> ants) {
        int guardCount = 0;
        for (Guard g: guards) {
            guardCount += g.getCount();
        }
        int newCount = strategy.getCountOfGuards(ants) - guardCount;
        if (newCount > 0) {
            for (Ant ant: justBorn) {
                for (Guard guard: guards) {
                    boolean shallAssign = guards.size() == 1 || guard.isNear(ant);
                    if (guard.isRequired() && guard.isHillAlive() && shallAssign) {
                        guard.assign(ant);
                        break;
                    }
                }
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
            }
        }
    }

    private void rushTheHill(List<Ant> ants) {
        //TODO: it does not know when the hill is destroyed
        Ants ants1 = getAnts();
        if (!ants1.getEnemyHills().isEmpty()) {
            Tile hill = ants1.getEnemyHills().iterator().next();
            int newRushers = strategy.getCountOfRushers(ants);
            final List<Ant> canAttackTheHill = new ArrayList<Ant>();
            final List<Integer> distances = new ArrayList<Integer>();
            for (Ant ant: ants) {
                if (newRushers > 0 && !ant.isHarvesting()) {
                    canAttackTheHill.add(ant);
                    distances.add(ants1.getDistance(ant.getPosition(), hill));
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
            for (int i = 0; i < canAttackTheHill.size() && i < newRushers; i++) {
                canAttackTheHill.get(i).doAttackTheHill(hill);
            }
        }
    }

    private void inspectNewArea(List<Ant> freeAnts) {
        for (Ant ant: freeAnts) {
            if (ant.isBusy()) continue;
            Tile position = ant.getPosition();
            int dx = 0, dy = 0;
            switch (Aim.values()[new Random().nextInt(4)]) {
                case NORTH: dy = +10; break;
                case EAST: dx = +10; break;
                case WEST: dx = -10; break;
                case SOUTH: dy = -10; break;
            }
            Tile tile = getAnts().normalize(position.getRow() + dy, position.getCol() + dx);
            ant.goAndSeeAround(tile);
        }

    }

    private void harvestFood(List<Ant> ants, List<Tile> unorderedFood) {
        for (Ant ant: ants) {
            if (ant.isRushing()) {
                //just ignore
            } else if (ant.isHarvesting() /*&& is harverst*/) {
                unorderedFood.remove(ant.getTarget());
            }
            else {
                for (Tile food: unorderedFood) {
                    if (ant.see(food) && ant.harvestFood(food)) {
                        unorderedFood.remove(food);
                        break;
                    }
                }
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

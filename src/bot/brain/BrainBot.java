package bot.brain;

import bot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrainBot extends Bot {


    private static final int ME = 0;
    private Strategy strategy;

    public BrainBot(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void beforeUpdate() {
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
            rushTheHill(this.ants);
            harvestFood(this.ants, unorderedFood);
            inspectNewArea(this.ants);

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
            for (Ant ant: ants) {
                if (newRushers > 0 && !ant.isHarvesting()) {
                    ant.doAttackTheHill(hill);
                    newRushers--;
                }
                if (newRushers < 0 && ant.isRushing()) {
                    ant.done();
                    newRushers++;
                }
                if (newRushers == 0) break;
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
            Tile tile = getAnts().normalize(position.getCol() + dx, position.getRow() + dy);
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
    public void addAnt(int row, int col, int owner) {
        super.addAnt(row, col, owner);
        if (owner == ME) {
            boolean found = false;
            for (Ant ant: oldAnts) {
                if (ant.isOn(row, col)) {
                    this.ants.add(ant);
                    this.oldAnts.remove(ant);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.ants.add(new Ant(new Tile(row, col), getAnts()));
            }
        }
    }


}

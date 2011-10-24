package bot.brain;

import bot.*;

import java.util.*;

public class Ant {

    private Tile position;
    private Ants ants;
    private static int NR = 1;
    private int nr = NR++;
    private boolean moved = false;
    private List<AntListener> listeners = new ArrayList<AntListener>();

    private LinkedList<AntOrder> orders = new LinkedList<AntOrder>();

    public Ant(Tile position, Ants ants) {
        this.position = position;
        this.ants = ants;
        orders.push(new NothingOrder(this));
    }

    public boolean isOn(Tile tile) {
        return position.equals(tile);
    }

    public boolean isOn(int row, int col) {
        return position.getCol() == col && position.getRow() == row;
    }

    public boolean move(Aim direction) {
        Tile newPos = ants.getTile(position, direction);
        Ilk newPosIlk = ants.getIlk(newPos);
        if (newPosIlk.isPassable() && newPosIlk != Ilk.MY_ANT) {
            ants.issueOrder(position, direction);
            this.position = newPos;
            moved = true;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean harvestFood(Tile tile) {
        return addOrderIfFeasible(new HarvestOrder(ants, this, tile));
    }

    private boolean addOrderIfFeasible(AntOrder order) {
        if (order.isFeasible()) {
            orders.push(order);
            return true;
        }
        else {
            return false;
        }
    }

    public void goAndSeeAround(Tile tile) {
        addOrderIfFeasible(new MoveToPointOrder(ants, this, tile));
    }

    public boolean isHarvesting() {
        return currentOrder().isHarvesting();
    }

    public void update() {
        if (!moved)
            currentOrder().step();
    }

    private AntOrder currentOrder() {
        return orders.peek();
    }

    public void done() {
        orders.pop();
    }


    public boolean isBusy() {
        return currentOrder().isBusy();
    }

    public Tile getPosition() {
        return position;
    }

    public boolean see(Tile food) {
        return ants.getDistance(position, food) <= ants.getViewRadius2();
    }

    public void die() {
        ants.log(this + " =(");
        for (AntListener listener: listeners) {
            listener.onDie(this);
        }
        listeners.clear();
    }
    
    public String toString() {
        return "Ant#" + nr + "(" + position + ")";
    }

    public Tile getTarget() {
        return currentOrder().getTarget();
    }


    public void onNewTurn() {
        moved = false;
    }

    private Map<Tile, Integer> hillAttacked = new HashMap<Tile, Integer>();
    private static final int TRIES_LIMIT = 10;

    public boolean doAttackTheHill(Tile hill) {
        Integer count = hillAttacked.get(hill);
        if (count == null) count = 1; else count++;
        if (count > TRIES_LIMIT) {
            ants.log(this + " tried to attack the hill at " + hill + " " + TRIES_LIMIT + " times, but failed");
            hillAttacked.put(hill, null);
            return false;
        }
        hillAttacked.put(hill, count);
        addOrderIfFeasible(new AttackHill(ants, this, hill));
        return isRushing();
    }


    public boolean isRushing() {
        return currentOrder().isRushing();
    }

    public void addListener(AntListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AntListener listener) {
        this.listeners.add(listener);
    }



}

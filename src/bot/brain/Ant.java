package bot.brain;

import bot.*;

import java.util.*;

public class Ant {

    private Tile position;
    private Ants ants;
    private static int NR = 1;
    private int nr = NR++;
    private boolean moved = false;
    private View visibleView;
    private View nearestView;
    private View soonAttackable;
    private Aim lastDirection = null;
    //private View attackView;
    private List<AntListener> listeners = new ArrayList<AntListener>();

    private LinkedList<AntOrder> orders = new LinkedList<AntOrder>();

    public Ant(Tile position, Ants ants) {
        this.position = position;
        this.ants = ants;
        orders.push(new NothingOrder(this));
        visibleView = new View(ants, position, ants.getViewRadius2());
        nearestView = new View(ants, position, 3);
        soonAttackable = new View(ants, position, ants.getAttackRadius2());
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
            lastDirection = direction;
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
        visibleView.setPoint(position);
        nearestView.setPoint(position);
        Tile tile = position;
        if (lastDirection != null) {
            tile = ants.getTile(tile, lastDirection);
            tile = ants.getTile(tile, lastDirection);
        }
        soonAttackable.setPoint(tile);
        lastDirection = null;
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
        addOrderIfFeasible(new AttackHill(ants, this, hill, visibleView));
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


    //returns -1 if not visible
    public int getDistanceToVisibleObject(Tile food) {
        int distance = ants.getDistance(position, food);
        if (distance > ants.getViewRadius2()) {
            return -1;
        }
        return distance;
    }


    public List<Tile> getNearestFriends() {
        return nearestView.getFriends();
    }

    public List<Tile> getVisibleEnemies() {
        return visibleView.getEnemies();
    }

    public List<Tile> getSoonAttackableEnemies() {
        return soonAttackable.getEnemies();
    }

    public boolean isMaxFriendsNear() {
        return getNearestFriendsCount() >= 3;
    }

    public int getNearestFriendsCount() {
        return nearestView.getFriendsCount();
    }

    public int getVisibleEnemiesCount() {
        return visibleView.getEnemiesCount();
    }


    public int getSoonAttackableEnemiesCount() {
        return soonAttackable.getEnemiesCount();
    }

    public void setLastDirection(Aim aim) {
        lastDirection = aim;
    }
}

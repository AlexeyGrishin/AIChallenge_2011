package bot.brain;

import bot.*;

import java.util.LinkedList;

public class Ant {

    private Tile position;
    private Ants ants;
    private static int NR = 1;
    private int nr = NR++;
    private boolean moved = false;

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

    public void doAttackTheHill(Tile hill) {
        addOrderIfFeasible(new AttackHill(ants, this, hill));
    }


    public boolean isRushing() {
        return currentOrder().isRushing();
    }
}

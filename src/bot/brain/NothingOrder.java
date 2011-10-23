package bot.brain;

import bot.Tile;

public class NothingOrder implements AntOrder {

    private Ant ant;

    public NothingOrder(Ant ant) {
        this.ant = ant;
    }

    public void step() {
        //do nothing
    }

    public boolean isBusy() {
        return false;
    }

    public boolean isHarvesting() {
        return false;
    }

    public boolean isFeasible() {
        return true;
    }

    public Tile getTarget() {
        return ant.getPosition();
    }

    public boolean isRushing() {
        return false;
    }
}

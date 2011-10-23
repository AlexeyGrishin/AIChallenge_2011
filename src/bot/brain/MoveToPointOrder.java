package bot.brain;

import bot.Ants;
import bot.Tile;

public class MoveToPointOrder extends MoveOrder {

    public MoveToPointOrder(Ants ants, Ant ant, Tile target) {
        super(ants, ant, target);
    }

    @Override
    protected boolean isDone() {
        return isTargetReached();
    }

    public boolean isHarvesting() {
        return false;
    }

}

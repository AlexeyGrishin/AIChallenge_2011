package bot.brain;

import bot.Ants;
import bot.Ilk;
import bot.Tile;

public class HarvestOrder extends MoveOrder {

    public HarvestOrder(Ants ants, Ant ant, Tile target) {
        super(ants, ant, target);

    }

    @Override
    protected boolean isDone() {
        return getTargetIlk() != Ilk.FOOD || isTargetReached();
    }

    public boolean isHarvesting() {
        return true;
    }
}

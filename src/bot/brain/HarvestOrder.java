package bot.brain;

import bot.Ants;
import bot.Ilk;
import bot.Tile;

public class HarvestOrder extends MoveOrder {

    public HarvestOrder(Ants ants, Ant ant, Tile target) {
        super(ants, ant, target);

    }

    protected boolean isNormalDistanceFromTarget(int distance2) {
        return distance2 <= 2;
    }

    @Override
    protected boolean isDone() {
        return getTargetIlk() != Ilk.FOOD || isTargetReached();
    }

    public boolean isHarvesting() {
        return true;
    }
}

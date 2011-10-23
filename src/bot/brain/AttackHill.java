package bot.brain;

import bot.Ants;
import bot.Ilk;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.MoveOrder;

public class AttackHill extends MoveOrder {

    protected AttackHill(Ants ants, Ant ant, Tile target) {
        super(ants, ant, target);
    }

    @Override
    protected boolean isDone() {
        return getTargetIlk() == Ilk.MY_ANT;
    }

    protected void onMovementImpossible() {
        findPath();
        if (!isFeasible()) {
            cancel();
        }
    }


    public boolean isHarvesting() {
        return true;
    }

    @Override
    public boolean isRushing() {
        return true;
    }
}

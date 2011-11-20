package bot.brain;

import bot.Ants;
import bot.Ilk;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.MoveOrder;

public class AttackHill extends MoveOrder {

    private View view;

    protected AttackHill(Ants ants, Ant ant, Tile target, View visibleView) {
        super(ants, ant, target);
        this.view = visibleView;
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

    @Override
    public void step() {
        if (iWillSurvive()) {
            super.step();
        }
        else {
            super.stepBack();
        }
    }

    private boolean iWillSurvive() {
        //if (true) return true;
        Ant ant = getAnt();
        int friendsCount = ant.getNearestFriendsCount() + 1;// + me
        int enemiesCount = ant.getSoonAttackableEnemiesCount();
        log("friendsCount = " + friendsCount + ", enemiesCount = " + enemiesCount);
        log("friends = " + ant.getNearestFriends() + ", enemies = " + ant.getSoonAttackableEnemies());
        if (friendsCount >= enemiesCount || ant.isMaxFriendsNear())
            return true;
        log("I do not want to die... wait for friends!");
        return false;
    }

    public boolean isHarvesting() {
        return true;
    }

    @Override
    public boolean isRushing() {
        return true;
    }
}

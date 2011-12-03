package bot2;

import pathfinder.MathCache;

public class GameSettings {
    private int attackRadius, attackRadius2;
    private int viewRadius, viewRadius2;
    private int currentTurn = 0;
    private long turnStart = 0;
    private long turnTime;

    public GameSettings(int attackRadius2, int viewRadius2, int turnTime) {
        this.attackRadius2 = attackRadius2;
        this.attackRadius = (int)Math.floor(MathCache.sqrt(attackRadius2));
        this.viewRadius2 = viewRadius2;
        this.viewRadius = (int)Math.floor(MathCache.sqrt(viewRadius2));
        this.turnTime = turnTime;
    }

    public int getAttackRadius() {
        return attackRadius;
    }

    public int getAttackRadius2() {
        return attackRadius2;
    }

    public int getViewRadius() {
        return viewRadius;
    }

    public int getViewRadius2() {
        return viewRadius2;
    }

    public void beforeTurn() {
        currentTurn++;
        turnStart = System.currentTimeMillis();
    }

    public long getRemaining() {
        return (turnTime - (System.currentTimeMillis() - turnStart));
    }

    public long getTurnTime() {
        return turnTime;
    }

    public boolean writeFieldToLog() {
        return true;
    }

    public int getTurn() {
        return currentTurn;
    }
}

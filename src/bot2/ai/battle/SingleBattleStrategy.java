package bot2.ai.battle;

public class SingleBattleStrategy {

    public final boolean reviewOnlyStandEnemies;
    public final boolean allowOurAntsToStand;
    public final int maxGroupSize;

    public SingleBattleStrategy(boolean reviewOnlyStandEnemies, boolean allowOurAntsToStand, int maxGroupSize) {
        this.reviewOnlyStandEnemies = reviewOnlyStandEnemies;
        this.allowOurAntsToStand = allowOurAntsToStand;
        this.maxGroupSize = maxGroupSize;
    }
}

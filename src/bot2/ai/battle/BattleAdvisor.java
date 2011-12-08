package bot2.ai.battle;

public interface BattleAdvisor<P> {

    public boolean isUnderAttack(P point1, P point2);

}

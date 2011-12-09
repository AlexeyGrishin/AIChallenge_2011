package bot2.ai;

import bot2.ai.areas.FieldArea;
import bot2.ai.battle.BattleCase;
import bot2.ai.battle.BattleCaseResolution;
import bot2.ai.battle.SingleBattleStrategy;
import bot2.map.FieldPoint;

import java.util.List;

public interface GameStrategy {

    public boolean shallRevisit(FieldArea area);

    public int getVisitRank(int visitedAgo);

    public BattleCase<FieldPoint> select(List<BattleCase<FieldPoint>> cases, List<FieldPoint> ours, List<FieldPoint> enemies);

    public SingleBattleStrategy getSingleBattleStrategy(List<FieldPoint> ourAntsInBattle, List<FieldPoint> enemiesInBattle);
}

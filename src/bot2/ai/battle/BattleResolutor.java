package bot2.ai.battle;

import common.MapList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleResolutor<P> {

    public static class BattleResolution {
        int ourLost = 0;
        int enemiesLost = 0;
        boolean enemiesStand = false;
    }

    public BattleResolution resolute(List<BattleUnitStep<P>> ourSteps, List<BattleUnitStep<P>> enemiesSteps, BattleAdvisor<P> advisor) {
        Map<BattleUnit<P>, Integer> weakness = new HashMap<BattleUnit<P>, Integer>();
        MapList<BattleUnit<P>, BattleUnit<P>> attackedBy = new MapList<BattleUnit<P>, BattleUnit<P>>();
        //1. setup references
        for (BattleUnitStep<P> our: ourSteps) {
            P point = our.getPoint();
            for (BattleUnitStep<P> enemy: enemiesSteps) {
                if (advisor.isUnderAttack(point, enemy.getPoint())) {
                    attackedBy.put(our.getUnit(), enemy.getUnit());
                    attackedBy.put(enemy.getUnit(), our.getUnit());
                }
            }
        }
        //2. calculate weakness
        for (BattleUnitStep<P> our: ourSteps) {
            weakness.put(our.getUnit(), attackedBy.get(our.getUnit()).size());
        }
        for (BattleUnitStep<P> enemy: enemiesSteps) {
            weakness.put(enemy.getUnit(), attackedBy.get(enemy.getUnit()).size());
        }
        //3. calculate resolution
        BattleResolution resolution = new BattleResolution();
        resolution.enemiesStand = areEnemiesStand(enemiesSteps);
        resolution.enemiesLost = getDied(enemiesSteps, weakness, attackedBy);
        resolution.ourLost = getDied(ourSteps, weakness, attackedBy);
        return resolution;

    }

    private int getDied(List<BattleUnitStep<P>> oneSideSteps, Map<BattleUnit<P>, Integer> weakness, MapList<BattleUnit<P>, BattleUnit<P>> attackedBy) {
        int dies = 0;
        for (BattleUnitStep<P> our: oneSideSteps) {
            int ourWeakness = weakness.get(our.getUnit());
            int minEnemyWeakness = -1;
            for (BattleUnit<P> enemy: attackedBy.get(our.getUnit())) {
                Integer enemyWeakness = weakness.get(enemy);
                if (minEnemyWeakness == -1 || minEnemyWeakness > enemyWeakness) {
                    minEnemyWeakness = enemyWeakness;
                }
            }
            if (ourWeakness > 0 && minEnemyWeakness <= ourWeakness) {
                dies ++;
            }
        }
        return dies;
    }

    private boolean areEnemiesStand(List<BattleUnitStep<P>> enemiesSteps) {
        for (BattleUnitStep<P> step: enemiesSteps) {
            if (!step.isStand()) return false;
        }
        return true;
    }

}

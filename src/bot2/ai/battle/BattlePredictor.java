package bot2.ai.battle;

import bot2.Logger;
import common.MapList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BattlePredictor<P> {

    private BattleResolutor<P> resolutor = new BattleResolutor<P>();

    public List<BattleCase<P>> predictBattle(List<BattleUnit<P>> ourUnits, List<BattleUnit<P>> enemies, BattleAdvisor<P> advisor) {
        List<BattleCase<P>> cases = new ArrayList<BattleCase<P>>();

        MapList<P, BattleUnitStep<P>> points = toPointsMap(ourUnits);
        Set<P> pointsUnderAttack = new HashSet<P> ();
        Set<P> attackersPoints = new HashSet<P>();

        for (BattleUnit<P> unit: enemies) {
            for (BattleUnitStep<P> step: unit.getPossibleSteps()) {
                P enemyPoints = step.getPoint();
                for (P point: points.getKeys()) {
                    if (advisor.isUnderAttack(enemyPoints, point)) {
                        attackersPoints.add(enemyPoints);
                        pointsUnderAttack.add(point);
                    }
                }
            }
        }

        //here we have steps and can iterate over them
        BattleUnits<P> ourUnitsIterator = new BattleUnits<P>(ourUnits, pointsUnderAttack);

        for (List<BattleUnitStep<P>> unitSteps: ourUnitsIterator) {
            BattleCase<P> bCase = new BattleCase<P>(unitSteps);
            BattleUnits<P> enemyUnitsIterator = new BattleUnits<P>(enemies, attackersPoints);
            for (List<BattleUnitStep<P>> enemiesSteps: enemyUnitsIterator) {
                BattleResolutor.BattleResolution resolution = resolutor.resolute(unitSteps, enemiesSteps, advisor);
                bCase.resolution.onResolution(resolution.ourLost, resolution.enemiesLost, resolution.enemiesStand);
            }
            cases.add(bCase);
        }

        return cases;
    }

    private MapList<P, BattleUnitStep<P>> toPointsMap(List<BattleUnit<P>> ourUnits) {
        MapList<P, BattleUnitStep<P>> points = new MapList<P, BattleUnitStep<P>>();
        for (BattleUnit<P> unit: ourUnits) {
            for (BattleUnitStep<P> step: unit.getPossibleSteps()) {
                points.put(step.getPoint(), step);
            }
        }
        return points;
    }

}

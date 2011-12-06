package bot2.ai.battle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BattleUnits<P> {

    private List<BattleUnit<P>> units;
    private List<LinkedList<BattleUnitStep<P>>> steps;

    public BattleUnits(List<BattleUnit<P>> units, Collection<BattleUnitStep<P>> meaningfulSteps) {
        this.units = new ArrayList<BattleUnit<P>>(units);
        this.steps = new ArrayList<LinkedList<BattleUnitStep<P>>>();
        for (BattleUnit<P> unit: units) {
            LinkedList<BattleUnitStep<P>> unitSteps = new LinkedList<BattleUnitStep<P>>();
            for (BattleUnitStep<P> step: unit.getPossibleSteps()) {
                if (step.isStand() || meaningfulSteps.contains(step)) {
                    unitSteps.add(step);
                }
            }
        }
    }

    //public List<BattleUnitStep<P>>
}

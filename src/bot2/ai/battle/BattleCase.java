package bot2.ai.battle;

import java.util.List;

public class BattleCase<P> {

    public final List<BattleUnitStep<P>> ourUnitsSteps;
    public final BattleCaseResolution resolution = new BattleCaseResolution();
    public Object flag = null; //just for debug

    public BattleCase(List<BattleUnitStep<P>> ourUnitsSteps) {
        this.ourUnitsSteps = ourUnitsSteps;
    }

    public String toString() {
        return ourUnitsSteps.toString() + "[" + resolution + "]";
    }
}

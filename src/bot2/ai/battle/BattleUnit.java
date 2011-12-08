package bot2.ai.battle;

import java.util.List;

public interface BattleUnit<P> {

    /**
     * First one shall be "stand" step
     * @return
     */
    public List<BattleUnitStep<P>> getPossibleSteps();

    public P getPoint();
}

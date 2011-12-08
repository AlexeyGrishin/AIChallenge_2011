package bot2.ai.battle;

public interface BattleUnitStep<P> {

    BattleUnit<P> getUnit();

    /**
     *
     * @param anotherStep
     * @return false if this step cannot be performed with specified step
     */
    boolean isCompatibleWith(BattleUnitStep<P> anotherStep);

    P getPoint();

    /**
     *
     * @return true if this step is "do nothing" step (stand at same position)
     */
    boolean isStand();
}

package bot2.ai.battle;

import bot2.map.Direction;
import bot2.map.FieldPoint;

public class AntStep implements BattleUnitStep<FieldPoint> {

    private AntBattleUnit ant;
    private FieldPoint thisPoint;
    private Direction dir;

    public AntStep(AntBattleUnit ant, FieldPoint thisPoint, Direction dir) {
        this.ant = ant;
        this.thisPoint = thisPoint;
        this.dir = dir;
    }

    public BattleUnit<FieldPoint> getUnit() {
        return ant;
    }

    public boolean isCompatibleWith(BattleUnitStep<FieldPoint> anotherStep) {
        return !anotherStep.getPoint().equals(thisPoint);
    }

    public FieldPoint getPoint() {
        return thisPoint;
    }

    public boolean isStand() {
        return thisPoint == ant.getPoint();
    }

    public String toString() {
        return ant.toString() + (isStand() ? "-" : dir.getDirChar());
    }
}

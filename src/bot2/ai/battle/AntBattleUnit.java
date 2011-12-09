package bot2.ai.battle;

import bot2.map.Direction;
import bot2.map.Field;
import bot2.map.FieldPoint;

import java.util.ArrayList;
import java.util.List;

public class AntBattleUnit implements BattleUnit<FieldPoint> {

    private FieldPoint point;
    private List<BattleUnitStep<FieldPoint>> possibleSteps;

    public AntBattleUnit(FieldPoint point, Field field, boolean allowStand, boolean onlyStand) {
        this.point = point;
        possibleSteps = new ArrayList<BattleUnitStep<FieldPoint>>(5);
        if (allowStand) {
            addStandStep(point);
        }
        if (!onlyStand) {
            for (Direction dir: Direction.values()) {
                FieldPoint dest = field.getPoint(point, dir);
                if (field.getItem(dest).isPassable()) {
                    possibleSteps.add(new AntStep(this, dest, dir));
                }
            }
        }
        if (possibleSteps.isEmpty()) {
            addStandStep(point);
        }
    }

    private void addStandStep(FieldPoint point) {
        possibleSteps.add(new AntStep(this, point, null));
    }

    public List<BattleUnitStep<FieldPoint>> getPossibleSteps() {
        return possibleSteps;
    }

    public FieldPoint getPoint() {
        return point;
    }

    public String toString() {
        return point.toString();
    }

}

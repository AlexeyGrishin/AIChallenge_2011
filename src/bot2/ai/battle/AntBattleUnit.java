package bot2.ai.battle;

import bot2.map.Direction;
import bot2.map.Field;
import bot2.map.FieldPoint;

import java.util.ArrayList;
import java.util.List;

public class AntBattleUnit implements BattleUnit<FieldPoint> {

    private FieldPoint point;
    private List<BattleUnitStep<FieldPoint>> possibleSteps;

    public AntBattleUnit(FieldPoint point, Field field) {
        this.point = point;
        possibleSteps = new ArrayList<BattleUnitStep<FieldPoint>>(5);
        possibleSteps.add(new AntStep(this, point, null));
        for (Direction dir: Direction.values()) {
            FieldPoint dest = field.getPoint(point, dir);
            if (field.getItem(dest).isPassable()) {
                possibleSteps.add(new AntStep(this, dest, dir));
            }
        }
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

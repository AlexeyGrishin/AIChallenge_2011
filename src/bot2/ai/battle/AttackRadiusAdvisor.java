package bot2.ai.battle;

import bot2.GameSettings;
import bot2.map.Field;
import bot2.map.FieldPoint;

public class AttackRadiusAdvisor implements BattleAdvisor<FieldPoint> {
    private Field field;
    private GameSettings settings;

    public AttackRadiusAdvisor(Field field, GameSettings settings) {
        this.field = field;
        this.settings = settings;
    }

    public boolean isUnderAttack(FieldPoint point1, FieldPoint point2) {
        return field.getDistance2(point1, point2) <= settings.getAttackRadius2();
    }
}

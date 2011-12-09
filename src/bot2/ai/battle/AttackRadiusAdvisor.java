package bot2.ai.battle;

import bot2.GameSettings;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.areas.CircleArea;
import bot2.map.areas.QuickArea;

public class AttackRadiusAdvisor implements BattleAdvisor<FieldPoint> {
    private Field field;
    private GameSettings settings;
    private int attackRadius2;
    private QuickArea quickArea;

    public AttackRadiusAdvisor(Field field, GameSettings settings) {
        this.field = field;
        this.settings = settings;
        attackRadius2 = settings.getAttackRadius2();
        quickArea = new CircleArea(field, attackRadius2).toQuickArea();
    }

    public boolean isUnderAttack(FieldPoint point1, FieldPoint point2) {
        return quickArea.containsPoth(point1, point2);
    }
}

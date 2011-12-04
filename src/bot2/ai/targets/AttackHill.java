package bot2.ai.targets;

import bot2.ai.Hills;
import bot2.ai.HillsHelper;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.View;

public class AttackHill extends DefaultTarget {

    private HillsHelper hillsHelper;

    public AttackHill(FieldPoint target, View field, HillsHelper hillsHelper) {
        super(target, field);
        this.hillsHelper = hillsHelper;
    }

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return !hillsHelper.hasEnemyHill(getTarget());
    }
}

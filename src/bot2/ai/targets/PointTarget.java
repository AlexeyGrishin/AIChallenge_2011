package bot2.ai.targets;

import bot2.map.FieldPoint;
import bot2.map.View;
import pathfinder.PointHelper;

public class PointTarget extends DefaultTarget {

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return location.equals(getTarget());
    }

    public PointTarget(FieldPoint target, View field) {
        super(target, field);
    }
}

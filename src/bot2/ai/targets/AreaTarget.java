package bot2.ai.targets;

import bot2.ai.areas.FieldArea;
import bot2.map.FieldPoint;
import bot2.map.View;

public class AreaTarget extends DefaultTarget {
    private FieldArea area;
    private boolean done = false;

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return done || area.containsPoint(location);//location.equals(getTarget());
    }

    public AreaTarget(FieldArea target, View field) {
        super(target.getCenter(), field);
        this.area = target;
    }

    @Override
    protected boolean shallContinueOnPathProblems(FieldPoint location, FieldPoint expected) {
        this.done = area.containsPoint(location);
        return done;
    }
}

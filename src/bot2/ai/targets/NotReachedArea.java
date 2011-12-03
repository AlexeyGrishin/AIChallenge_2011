package bot2.ai.targets;

import bot2.ai.areas.FieldArea;
import bot2.map.FieldPoint;
import bot2.map.View;

public class NotReachedArea extends DefaultTarget {

    private FieldArea area;

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return getTarget().equals(location) || area.isReached();
    }

    public NotReachedArea(FieldArea area, View field) {
        super(area.getCenter(), field);
        this.area = area;
    }
}

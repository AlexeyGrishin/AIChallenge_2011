package bot2.ai.targets;

import bot2.ai.areas.AreasMapper;
import bot2.ai.areas.FieldArea;
import bot2.map.FieldPoint;
import bot2.map.View;

public class FindKnownArea extends NotReachedArea {
    private AreasMapper mapper;

    public FindKnownArea(FieldArea area, View visibleView, AreasMapper mapper) {
        super(area, visibleView);
        this.mapper = mapper;
    }

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return super.isTargetFound(location) || mapper.get(location) != null;
    }
}

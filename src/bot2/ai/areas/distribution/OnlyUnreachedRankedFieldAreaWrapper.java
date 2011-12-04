package bot2.ai.areas.distribution;

import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;

public class OnlyUnreachedRankedFieldAreaWrapper extends RankedFieldAreaWrapper {
    public OnlyUnreachedRankedFieldAreaWrapper(AreasPathHelper pathHelper) {
        super(pathHelper);
    }

    @Override
    protected RankedFieldArea produce(FieldArea area, AreasPathHelper pathHelper) {
        return new OnlyUnreachedRankedFieldArea(this, area, pathHelper);
    }
}

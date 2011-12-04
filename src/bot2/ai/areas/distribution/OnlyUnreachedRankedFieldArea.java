package bot2.ai.areas.distribution;

import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;

public class OnlyUnreachedRankedFieldArea extends RankedFieldArea {
    public OnlyUnreachedRankedFieldArea(AreaWrapper<FieldArea> wrapper, FieldArea area, AreasPathHelper pathHelper) {
        super(wrapper, area, pathHelper);
    }

    @Override
    protected int getRequiredAmount(int defaultAmount, FieldArea area) {
        return area.isReached() ? 0 : 999;
    }
}

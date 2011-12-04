package bot2.ai.areas.distribution;

import bot2.ai.areas.AreaHelper;
import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;

public class RankedFieldAreaWrapper extends DefaultWrapper<FieldArea> {

    private AreasPathHelper pathHelper;

    public RankedFieldAreaWrapper(AreasPathHelper pathHelper) {
        this.pathHelper = pathHelper;
    }

    public DistributableArea wrap(FieldArea area) {
        return new RankedFieldArea(this, area, pathHelper);
    }

    public FieldArea unwrap(DistributableArea area) {
        return ((RankedFieldArea)area).getArea();
    }

}

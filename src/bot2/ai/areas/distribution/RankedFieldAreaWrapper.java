package bot2.ai.areas.distribution;

import bot2.ai.areas.FieldArea;

public class RankedFieldAreaWrapper extends DefaultWrapper<FieldArea> {

    public DistributableArea wrap(FieldArea area) {
        return new RankedFieldArea(this, area);
    }

    public FieldArea unwrap(DistributableArea area) {
        return ((RankedFieldArea)area).getArea();
    }

}

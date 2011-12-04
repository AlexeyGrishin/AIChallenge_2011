package bot2.ai.areas.distribution;

import bot2.ai.areas.AreaHelper;
import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;

import java.util.HashMap;
import java.util.Map;

public class RankedFieldAreaWrapper extends DefaultWrapper<FieldArea> {

    private AreasPathHelper pathHelper;
    private Map<FieldArea, RankedFieldArea> cache = new HashMap<FieldArea, RankedFieldArea>();

    public RankedFieldAreaWrapper(AreasPathHelper pathHelper) {
        this.pathHelper = pathHelper;
    }

    public DistributableArea wrap(FieldArea area) {
        if (area == null) return null;
        RankedFieldArea disArea = cache.get(area);
        if (disArea == null) {
            disArea = produce(area, pathHelper);
            cache.put(area, disArea);
        }
        return disArea;
    }

    protected RankedFieldArea produce(FieldArea area, AreasPathHelper pathHelper) {
        return new RankedFieldArea(this, area, pathHelper);
    }

    public FieldArea unwrap(DistributableArea area) {
        return ((RankedFieldArea)area).getArea();
    }

}

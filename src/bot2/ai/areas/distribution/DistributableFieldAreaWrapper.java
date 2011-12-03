package bot2.ai.areas.distribution;

import bot2.ai.areas.FieldArea;

import java.util.*;

public class DistributableFieldAreaWrapper extends DefaultWrapper<FieldArea> {

    private Map<FieldArea, DistributableFieldArea> map = new HashMap<FieldArea, DistributableFieldArea>();

    public DistributableArea wrap(FieldArea area) {
        DistributableFieldArea wrArea = map.get(area);
        if (wrArea == null) {
            wrArea = new DistributableFieldArea(area);
            map.put(area, wrArea);
        }
        return wrArea;
    }

    public FieldArea unwrap(DistributableArea area) {
        return ((DistributableFieldArea)area).getArea();
    }

}

package bot2.ai.areas.distribution;

import bot2.ai.areas.FieldArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DefaultWrapper<T> implements AreaWrapper<T> {


    public Collection<DistributableArea> wrap(Collection<? extends T> areas) {
        List<DistributableArea> wrAreas = new ArrayList<DistributableArea>(areas.size());
        for (T area: areas) {
            wrAreas.add(wrap(area));
        }
        return wrAreas;
    }
}

package bot2.ai.areas.distribution;

import bot2.ai.areas.FieldArea;

import java.util.Collection;

public interface AreaWrapper<T> {

    public DistributableArea wrap(T area);

    public T unwrap(DistributableArea area);

    Collection<DistributableArea> wrap(Collection<? extends T> areas);
}

package bot2.ai.areas.distribution;

import java.util.Collection;

public interface Distributor {

    public void distribute(Collection<DistributableArea> areas, Collection<AreaWalker> walkers);
}

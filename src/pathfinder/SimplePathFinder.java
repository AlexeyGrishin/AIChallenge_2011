package pathfinder;

import java.util.Collection;

public interface SimplePathFinder<C> {

    public Collection<PathFinder.PathElement<C>> find(C from, C to);
}

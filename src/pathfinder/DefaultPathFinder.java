package pathfinder;

import java.util.Collection;

public class DefaultPathFinder<C> implements SimplePathFinder<C> {

    private PointHelper<C> helper;

    public DefaultPathFinder(PointHelper<C> helper) {
        this.helper = helper;
    }

    public Collection<PathFinder.PathElement<C>> find(C from, C to) {
        PathFinder<C> finder = new PathFinder<C>(helper, from, to);
        finder.findPath();
        return finder.getFoundPath();
    }
}

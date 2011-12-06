package pathfinder;

import pathfinder.PathFinder;
import pathfinder.PointHelper;

import java.util.*;

public class PathsCache<C> {

    private SimplePathFinder<C> finder;

    public PathsCache(SimplePathFinder<C> finder) {
        this.finder = finder;
    }

    class Key {
        C source;
        C target;

        Key(C source, C target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (!source.equals(key.source)) return false;
            if (!target.equals(key.target)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + target.hashCode();
            return result;
        }
    }


    private Map<Key, List<C>> paths = new HashMap<Key, List<C>>();


    public void reset() {
        paths.clear();
    }

    public List<C> getPath(C from, C to) {
        Key key = new Key(from, to);
        List<C> path = paths.get(key);
        if (path != null) {
            return path;
        }
        Key reverseKey = new Key(to, from);
        path = paths.get(reverseKey);
        if (path != null) {
            LinkedList<C> reversed = new LinkedList<C>();
            for (C area: path) {
                reversed.addFirst(area);
            }
            return reversed;
        }
        path = findPath(from, to);
        paths.put(key, path);
        return path;
    }

    public int getPathSize(C from, C to) {
        return getPath(from, to).size();
    }

    private List<C> findPath(C from, C to) {
        Collection<PathFinder.PathElement<C>> foundPath = finder.find(from, to);
        List<C> path = new ArrayList<C>(foundPath.size());
        for (PathFinder.PathElement<C> element: foundPath) {
            path.add(element.to);
        }
        if (path.get(path.size()-1) != to) {
            path.add(to);
        }
        return path;
    }

}

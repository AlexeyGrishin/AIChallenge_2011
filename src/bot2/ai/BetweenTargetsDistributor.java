package bot2.ai;

import bot2.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BetweenTargetsDistributor<S, T> {


    public static class SourceTarget<S, T> {
        public final S source;
        public final T target;

        public SourceTarget(S source, T target) {
            this.source = source;
            this.target = target;
        }
    }

    public interface DistanceMeasurer<S, T> {

        public static final int NOT_FOUND = -1;
        /**
         *
         * @param from
         * @param to
         * @return -1 if not reachable, otherwise - count of steps between
         */
        public int measureDistance(S from, T to);
    }


    public List<SourceTarget<S, T>> distribute(Collection<S> sources, Collection<T> targets, DistanceMeasurer<S, T> dist) {
        List<SourceTarget<S, T>> list = new ArrayList<SourceTarget<S, T>>(sources.size());
        for (S source: sources) {
            int minDistance = Integer.MAX_VALUE;
            T nearestTarget = null;
            for (T target: targets) {
                int distance = dist.measureDistance(source, target);
                Logger.log("  " + source + "->" + target + ": distance = " + distance);
                if (distance != DistanceMeasurer.NOT_FOUND && distance < minDistance) {
                    minDistance = distance;
                    nearestTarget = target;
                }
            }
            list.add(new SourceTarget<S, T>(source, nearestTarget));
        }
        return list;
    }
}

package bot2.ai.areas.distribution;

import java.util.Collection;

public interface DistributableArea extends Comparable<DistributableArea> {

    public Collection<DistributableArea> getNearestAreas();

    public int getRequiredAmount(int defaultAmount);

    /**
     *
     * @param area
     * @return -1 if current area has more priority than specified
     */
    public int compareTo(DistributableArea area);
}

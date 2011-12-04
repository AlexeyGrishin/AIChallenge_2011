package bot2.ai.areas.distribution;

import java.util.Collection;

public interface AreaWalker {

    /**
     *
     * @return area this walker going to walk into near time. includes current location
     */
    public Collection<DistributableArea> getDestinationAreas();

    /**
     *
     * @return area this walker currenly on (or some very near area) - used to estimate distance
     */
    public DistributableArea getLocation();
    /**
     *
     * @return true if walker moves somewhere
     */
    public boolean isInMove();

    /**
     * Makes walker walk to specified area
     * @param area
     */
    public void moveTo(DistributableArea area);

}

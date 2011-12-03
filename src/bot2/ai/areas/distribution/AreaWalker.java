package bot2.ai.areas.distribution;

public interface AreaWalker {

    /**
     *
     * @return area this walker walks to, or area it is currently in if it does not walk
     */
    public DistributableArea getDestinationArea();

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

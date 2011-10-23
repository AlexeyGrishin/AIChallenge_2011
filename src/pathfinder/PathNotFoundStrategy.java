package pathfinder;

public interface PathNotFoundStrategy {

    public void formNotFoundPath();

    public boolean isLimitReached(int count, long time);

}

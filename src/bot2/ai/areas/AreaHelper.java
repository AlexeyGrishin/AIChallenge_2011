package bot2.ai.areas;

import bot2.map.FieldPoint;

public interface AreaHelper {

    public boolean contains(FieldArea area, FieldPoint point);

    public boolean shallRevisit(FieldArea area);

    public int getVisitRank(int visitedAgo);
}

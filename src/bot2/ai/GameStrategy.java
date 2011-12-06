package bot2.ai;

import bot2.ai.areas.FieldArea;

public interface GameStrategy {

    public boolean shallRevisit(FieldArea area);

    public int getVisitRank(int visitedAgo);
}

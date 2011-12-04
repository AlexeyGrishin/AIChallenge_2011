package bot2.ai;

import bot2.map.FieldPoint;

public interface HillsHelper {

    public boolean hasOurHill(FieldPoint p);

    public boolean hasEnemyHill(FieldPoint p);
}

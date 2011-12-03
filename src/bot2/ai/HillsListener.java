package bot2.ai;

import bot2.map.FieldPoint;

public interface HillsListener {

    public void onOurHill(FieldPoint point);
    public void onOurHillDestroyed(FieldPoint point);

    public void onEnemyHill(FieldPoint point);
    public void onEnemyHillDestroyed(FieldPoint point);

}

package bot2.ai;

import bot2.map.FieldPoint;

public interface HillsListener {

    /**
     * Called each turn for each our alive hill
     */
    public void onOurHill(FieldPoint point);

    /**
     * Called once on our hill destroy
     */
    public void onOurHillDestroyed(FieldPoint point);

    /**
     * Called each turn for each enemy alive hill
     */
    public void onEnemyHill(FieldPoint point);

    /**
     * Called once on enemy hill destroy
     */
    public void onEnemyHillDestroyed(FieldPoint point);

}

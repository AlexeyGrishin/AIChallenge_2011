package bot2.ai.areas;

public class FieldAreaStat {

    private int enemies;
    private int alies;
    private int visitedTurnsAgo;
    private int food;
    private int foodGatheredTotal;
    private int walkingTotal;
    private int enemiesSeenTurnsAgo;

    private FieldArea area;

    public FieldAreaStat(FieldArea area) {
        this.area = area;
    }

    public void beforeUpdate() {
        enemies = 0;
        alies = 0;
        food = 0;
        visitedTurnsAgo++;
        enemiesSeenTurnsAgo++;
    }

    public void addAnt() {
        alies++;
        visitedTurnsAgo = 0;
        walkingTotal++;
    }

    public void addEnemy() {
        enemies++;
        enemiesSeenTurnsAgo = 0;
    }

    public void addFood() {
        food++;
    }

    public void onFoodGathered() {
        foodGatheredTotal++;
    }

    public int getEnemies() {
        return enemies;
    }

    public int getAlies() {
        return alies;
    }

    public int getVisitedTurnsAgo() {
        return visitedTurnsAgo;
    }

    public int getFood() {
        return food;
    }

    public int getFoodGatheredTotal() {
        return foodGatheredTotal;
    }

    public int getWalkingTotal() {
        return walkingTotal;
    }

    public int getEnemiesSeenTurnsAgo() {
        return enemiesSeenTurnsAgo;
    }

    public int getOpened() {
        return area.getNearAreasCollection().size();
    }

    public String toString() {
        return "visitedAgo: " + visitedTurnsAgo;
    }
}

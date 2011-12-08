package bot2.ai.battle;

public class BattleCaseResolution {

    public int maxLost = 0;
    public int maxEnemiesLost = 0;

    public int minLost = -1;
    public int minEnemiesLost = -1;

    public int standLost;
    public int standEnemiesLost;

    public void onResolution(int ourLost, int enemiesLost, boolean enemiesStand) {
        if (enemiesStand) {
            standLost = ourLost;
            standEnemiesLost = enemiesLost;
        }
        if (maxLost < ourLost) {
            maxLost = ourLost;
        }
        if (maxEnemiesLost < enemiesLost) {
            maxEnemiesLost = enemiesLost;
        }
        if (minLost == -1 || minLost > ourLost) {
            minLost = ourLost;
        }
        if (minEnemiesLost == -1 || minEnemiesLost > enemiesLost) {
            minEnemiesLost = enemiesLost;
        }

    }

    public String toString() {
        return "our: " + minLost + "/" + maxLost + ", enemies: " + minEnemiesLost + "/" + maxEnemiesLost;
    }

}

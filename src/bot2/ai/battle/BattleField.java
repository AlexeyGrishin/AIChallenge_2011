package bot2.ai.battle;

import java.util.Arrays;
import java.util.Collections;

public class BattleField {

    private int[][] battleField;
    private int ourSum, enemySum;
    private int enemiesCount;
    private int ourCount;

    public BattleField(int ours, int enemies) {
        battleField = new int[ours+1][enemies+1];
        enemySum = ours;
        ourSum = enemies;
        enemiesCount = enemies;
        ourCount = ours;
    }

    public void clear() {
        for (int our = 0; our <= ourCount; our++) {
            Arrays.fill(battleField[our], 0);
        }
    }

    public void attacked(int our, int enemy) {
        battleField[our][enemy] = 1;
        battleField[our][ourSum]++;
        battleField[enemySum][enemy]++;
    }

    public static class Dead {
        int our;
        int enemies;

        public Dead(int our, int enemies) {
            this.our = our;
            this.enemies = enemies;
        }

        public Dead() {
        }

        public boolean equals(Object o) {
            return ((Dead)o).enemies == enemies && ((Dead)o).our == our;
        }

        public String toString() {
            return "o: " + our + "/e:" + enemies;
        }
    }

    public Dead getDead() {
        Dead dead = new Dead();
        boolean enemiesDead[] = new boolean[enemiesCount];
        boolean ourDead[] = new boolean[ourCount];
        for (int our = 0; our < ourCount; our++) {
            int ourWeakness = battleField[our][ourSum];
            if (ourWeakness == 0) continue;
            for (int enemy = 0; enemy < enemiesCount; enemy++) {
                int enemyWeakness = battleField[enemySum][enemy];
                if (ourWeakness >= enemyWeakness) {
                    ourDead[our] = true;
                }
                if (ourWeakness <= enemyWeakness) {
                    enemiesDead[enemy] = true;
                }
            }
        }
        for (int unit = 0; unit < Math.max(ourCount, enemiesCount); unit++) {
            if (unit < enemiesCount && enemiesDead[unit]) dead.enemies++;
            if (unit < ourCount && ourDead[unit]) dead.our++;
        }
        return dead;
    }
}

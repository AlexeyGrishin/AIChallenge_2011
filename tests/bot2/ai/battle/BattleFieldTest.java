package bot2.ai.battle;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BattleFieldTest {

    @Test
    public void emptyField() {
        BattleField field = new BattleField(0, 0);
        assertDead(0, 0, field);
    }

    @Test
    public void twoAnts() {
        BattleField field = new BattleField(1, 1);
        field.attacked(0, 0);
        assertDead(1, 1, field);
    }

    @Test
    public void twoOurAnts_againstSingleEnemy() {
        BattleField field = new BattleField(2, 1);
        field.attacked(0, 0);
        field.attacked(1, 0);
        assertDead(0, 1, field);
    }

    @Test
    public void twoOurAnt_againstTwoEnemies() {
        BattleField field = new BattleField(1, 2);
        field.attacked(0, 0);
        field.attacked(0, 1);
        assertDead(1, 0, field);
    }

    @Test
    public void twoOurAnts_againstTwoEnemies() {
        BattleField field = new BattleField(2, 2);
        field.attacked(0, 0);
        field.attacked(1, 0);
        field.attacked(0, 1);
        field.attacked(0, 0);
        assertDead(2, 2, field);
    }

    @Test
    public void twoOurAnts_againstTwoEnemies_separately() {
        BattleField field = new BattleField(2, 2);
        field.attacked(0, 0);
        field.attacked(1, 1);
        assertDead(2, 2, field);
    }

    private void assertDead(int our, int enemies, BattleField field) {
        assertEquals(new BattleField.Dead(our, enemies), field.getDead());
    }

}

package logic;

import bot.Aim;
import bot.Bot;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.Order;
import bot.brain.View;
import util.MockAnts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMock {

    private Bot bot;
    private MockAnts mockAnts;

    public GameMock(Bot bot, MockAnts mockAnts) {
        this.bot = bot;
        this.mockAnts = mockAnts;
        for (Tile ant: mockAnts.getMyAnts()) {
            followAnt(ant);
        }
        for (Tile ant: mockAnts.getEnemyAnts()) {
            setEnemy(ant);
        }
        for (Tile food: mockAnts.getFoodTiles()) {
            setFood(food);
        }
    }

    private List<Tile> ants = new ArrayList<Tile>();
    private List<Tile> enemies = new ArrayList<Tile>();
    private List<Tile> food = new ArrayList<Tile>();
    private List<Tile> hills = new ArrayList<Tile>();
    private List<Tile> enemyHills = new ArrayList<Tile>();


    public void turn() {
        update();
        doTurn();
        processOrders();
    }

    public void processOrders() {
        List<Tile> newTiles = new ArrayList<Tile>();
        for (Order order: mockAnts.getOrders()) {
            Tile origin = order.getPosition();
            Tile result = mockAnts.getTile(origin, order.getAim());
            ants.remove(origin);
            newTiles.add(result);

            if (food.contains(result)) {
                food.remove(result);
            }
        }
        for (Tile tile: newTiles) {
            this.ants.add(tile);
        }
    }

    public void doTurn() {
        bot.doTurn();
    }

    public void update() {
        bot.beforeUpdate();
        for (Tile food: this.food) {
            bot.addFood(food.getRow(), food.getCol());
        }
        for (Tile ant: ants) {
            bot.addAnt(ant.getRow(), ant.getCol(), 0);
        }
        for (Tile ant: enemies) {
            bot.addAnt(ant.getRow(), ant.getCol(), 1);
        }
        for (Tile hill: hills) {
            bot.addHill(hill.getRow(), hill.getCol(), 0);
        }
        for (Tile hill: enemyHills) {
            bot.addHill(hill.getRow(), hill.getCol(), 1);
        }
        bot.afterUpdate();
    }

    public void setFood(Tile tile) {
        food.add(tile);
    }

    public void removeFood(Tile tile) {
        food.remove(tile);
    }

    public void followAnt(Tile tile) {
        ants.add(tile);
    }

    public List<Tile> getAnts() {
        return ants;
    }

    public Tile getAnt() {
        return ants.get(0);
    }

    public Tile getFood() {
        return food.get(0);
    }

    public boolean hasFood() {
        return food.isEmpty();
    }

    public void addHill(Tile hill) {
        hills.add(hill);
    }

    public void setEnemy(Tile enemy) {
        enemies.add(enemy);
    }

    public void removeEnemy(Tile enemy) {
        enemies.remove(enemy);
    }

    public void addEnemyHill(Tile tile) {
        enemyHills.add(tile);
    }

    public void moveEnemies(Aim direction) {
        List<Tile> newTiles = new ArrayList<Tile>();
        for (Tile enemy: new ArrayList<Tile>(enemies)) {
            newTiles.add(mockAnts.getTile(enemy, direction));
            removeEnemy(enemy);
        }
        for (Tile tile: newTiles) {
            setEnemy(tile);
        }
    }

    public void calculateDeath() {
        Set<Tile> toDeath = new HashSet<Tile>();
        for (Tile our: ants) {
            View view = new View(mockAnts, our, mockAnts.getAttackRadius2());
            int str = getStrength(view.getEnemiesCount());
            for (Tile enemy: view.getEnemies()) {
                View enemyView = new View(mockAnts, enemy, mockAnts.getAttackRadius2());
                int enemyStr = getStrength(enemyView.getFriendsCount());
                if (str < enemyStr) {
                    toDeath.add(our);
                }
            }
        }
        for (Tile enemy: enemies) {
            View view = new View(mockAnts, enemy, mockAnts.getAttackRadius2());
            int str = getStrength(view.getFriendsCount());
            for (Tile our: view.getFriends()) {
                View ourView = new View(mockAnts, our, mockAnts.getAttackRadius2());
                int ourStr = getStrength(ourView.getEnemiesCount());
                if (str < ourStr) {
                    toDeath.add(enemy);
                }
            }
        }
        System.out.println("To death: " + toDeath);
        enemies.removeAll(toDeath);
        ants.removeAll(toDeath);
    }

    private int getStrength(int count) {
        if (count > 0) {
            return 100/count;
        }
        return 200;
    }
}

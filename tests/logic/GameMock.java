package logic;

import bot.Bot;
import bot.Tile;
import bot.brain.Order;
import util.MockAnts;

import java.util.ArrayList;
import java.util.List;

public class GameMock {

    private Bot bot;
    private MockAnts mockAnts;
    public GameMock(Bot bot, MockAnts mockAnts) {
        this.bot = bot;
        this.mockAnts = mockAnts;
        for (Tile ant: mockAnts.getMyAnts()) {
            followAnt(ant);
        }
        for (Tile food: mockAnts.getFoodTiles()) {
            setFood(food);
        }
    }

    private List<Tile> ants = new ArrayList<Tile>();
    private List<Tile> enemies = new ArrayList<Tile>();
    private List<Tile> food = new ArrayList<Tile>();
    private List<Tile> hills = new ArrayList<Tile>();


    public void turn() {
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
        bot.afterUpdate();
        bot.doTurn();

        for (Order order: mockAnts.getOrders()) {
            Tile origin = order.getPosition();
            Tile result = mockAnts.getTile(origin, order.getAim());
            ants.remove(origin);
            ants.add(result);

            if (food.contains(result)) {
                food.remove(result);
            }
        }
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
}

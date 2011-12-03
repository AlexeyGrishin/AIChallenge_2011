package bot2;

import bot2.map.Direction;
import bot2.map.FieldPoint;
import bot2.map.Item;
import util.MockField;

import java.util.ArrayList;
import java.util.List;

public class MockGame {

    private Bot bot;
    private MockField field;

    public MockGame(Bot bot, MockField field) {
        this.bot = bot;
        this.field = field;
    }

    public void setExecutor(Orders.OrderExecutor executor) {
        this.executor = executor;
    }

    public class GO {
        private Item type;
        private int x;
        private int y;

        GO(Item type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public Item getType() {
            return type;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void move(Direction direction) {
            FieldPoint newPoint = field.getPoint(FieldPoint.point(x, y), direction);
            x = newPoint.x;
            y = newPoint.y;
        }
    }

    private Orders.OrderExecutor executor = null;

    private List<GO> objects = new ArrayList<GO>();
    private List<FieldPoint> hills = new ArrayList<FieldPoint>();

    public void doTurn() {
        bot.beforeUpdate();
        for (GO object: objects) {
            bot.updateVisible(object.x, object.y, object.type);
        }
        for (FieldPoint point: hills) {
            bot.updateVisibleHill(point.x, point.y, 0);
        }
        bot.afterUpdate();
        bot.doTurn().execute(new Orders.OrderExecutor() {
            public void move(int x, int y, Direction direction) {
                for (GO object: objects) {
                    if (object.x == x && object.y == y && object.type == Item.ANT) {
                        object.move(direction);
                        if (executor != null) {
                            executor.move(x, y, direction);
                        }
                    }
                }
            }
        });

    }


    public GO set(int x, int y, Item type) {
        GO go = new GO(type, x, y);
        this.objects.add(go);
        return go;
    }

    public void addOurHill(int x, int y) {
        hills.add(new FieldPoint(x, y));
    }

}

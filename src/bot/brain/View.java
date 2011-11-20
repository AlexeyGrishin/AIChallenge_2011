package bot.brain;

import bot.Ants;
import bot.Ilk;
import bot.Tile;
import pathfinder.MathCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View {


    static class ViewPoint {
        int dx, dy;

        ViewPoint(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        Tile getTile(Ants field, Tile tile) {
            return field.normalize(tile.getRow() + dy, tile.getCol() + dx);
        }
    }

    private static Map<Integer, List<ViewPoint>> view = new HashMap<Integer, List<ViewPoint>>();


    private static void initView(Ants ants, int radius2) {
        if (view.containsKey(radius2)) return;
        List<ViewPoint> points = new ArrayList<ViewPoint>(radius2*4);
        view.put(radius2, points);
        int radius = (int)Math.ceil(MathCache.sqrt(radius2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                long distance = MathCache.square(x) + MathCache.square(y);
                if (distance <= radius2) {
                    points.add(new ViewPoint(x, y));
                }
            }
        }
    }


    private Ants field;
    private Tile point;
    private int radius2;
    private boolean empty = true;

    private List<Tile> friends = new ArrayList<Tile>();
    private List<Tile> enemies = new ArrayList<Tile>();

    public View(Ants field, Tile point, int radius2) {
        initView(field, radius2);
        this.point = point;
        this.field = field;
        this.radius2 = radius2;
    }

    public void resetView() {
        enemies.clear();
        friends.clear();
        empty = true;
    }

    public int getEnemiesCount() {
        initLists();
        return enemies.size();
    }

    public void setPoint(Tile point) {
        this.point = point;
        resetView();
    }

    private void initLists() {
        if (empty) {
            for (ViewPoint p: view.get(radius2)) {
                Tile t = p.getTile(field, point);
                if (t.equals(point)) continue;//skip self

                Ilk ilk = field.getIlk(t);
                switch (ilk) {
                    case MY_ANT:
                        friends.add(t);
                        break;
                    case ENEMY_ANT:
                        enemies.add(t);
                        break;
                }
            }
            empty = false;
        }
    }

    public int getFriendsCount() {
        initLists();
        return friends.size();
    }

    public List<Tile> getFriends() {
        initLists();
        return friends;
    }

    public List<Tile> getEnemies() {
        initLists();
        return enemies;
    }
}

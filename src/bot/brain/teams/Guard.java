package bot.brain.teams;

import bot.Ants;
import bot.Tile;
import bot.brain.Ant;
import bot.brain.Group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Guard extends BaseTeam {

    private Group group = new Group();
    private Tile ourHill;
    private List<Tile> possiblePositions = new ArrayList<Tile>();

    public Guard(Tile ourHill) {
        this.ourHill = ourHill;
    }

    @Override
    public void setField(Ants ants) {
        super.setField(ants);
        group.setField(ants);
        initPossiblePositions();
    }

    /*private String[] map = new String[] {
            "  j g  ",
            "       ",
            "e a c k",
            "       ",
            "l d b f",
            "       ",
            "  h i  "
    };*/
    private String[] map = new String[] {
            "       ",
            "       ",
            "  a c  ",
            "       ",
            "  d b  ",
            "       ",
            "       "
    };
    private static final int XCENTER = 3, YCENTER = 3, OVERALLCOUNT = 4;

    private void initPossiblePositions() {
        Tile[] tiles = new Tile[OVERALLCOUNT];
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length(); col++) {
                char c = map[row].charAt(col);
                if (c != ' ') {
                    tiles[c - 'a'] = getField().normalize(ourHill.getRow() + row - YCENTER, ourHill.getCol() + col - XCENTER);
                }
            }
        }
        for (Tile tile: tiles) {
            if (getField().getIlk(tile).isPassable()) {
                /*PathFinder<Tile> finder = new PathFinder<Tile>(new AntsHelper(getField()), ourHill, tile);
                finder.setNotFoundStrategy(finder.limited(260, finder.RETURN_EMPTY));
                finder.findPath();
                if (!finder.getFoundPath().isEmpty()) {*/
                    possiblePositions.add(tile);
                    getField().log("Guard: " + tile);
                /*}*/
            }
        }
    }

    private final static int ALARM_TIME = 10;
    private int enemiesDetectedUsTurnsAgo = ALARM_TIME;

    public void doTurn() {
        checkDetection();
        Collection<Tile> enemies = group.findEnemiesInGroupVision(getAnts());
        if (!enemies.isEmpty()) {
            group.turnToEnemies(getAnts(), enemies);
        }
        else {
            if (!isHillInDanger()) {
                getField().log("Guard: hill " + ourHill + " is no more under danger");
            }
            if (getCount() > possiblePositions.size()) {
                detach(possiblePositions.size(), new ArrayList<Ant>());
            }
            group.backToPositions(getAnts(), limitPositions(getCount()));
        }
    }

    private void checkDetection() {
        for (Tile enemy: getField().getEnemyAnts()) {
            if (getField().getDistance(enemy, ourHill) <= getField().getViewRadius2()) {
                getField().log("Guard: enemies around hill " + ourHill);
                enemiesDetectedUsTurnsAgo = 0;
                return;
            }
        }
        enemiesDetectedUsTurnsAgo++;

    }

    public boolean isHillInDanger() {
        return enemiesDetectedUsTurnsAgo < ALARM_TIME;
    }

    public boolean isHillInDangerRightNow() {
        return enemiesDetectedUsTurnsAgo < 1;
    }

    @Override
    public void assign(Ant ant) {
        super.assign(ant);
    }

    private List<Tile> limitPositions(int count) {
        List<Tile> tiles = new ArrayList<Tile>();
        for (int i = 0; i < count; i++) {
            tiles.add(possiblePositions.get(i));
        }
        return tiles;
    }

    public boolean isRequired() {
        return isHillInDangerRightNow() || getCount() < possiblePositions.size();
    }

    public boolean isHillAlive() {
        return true;//getField().getMyHills().contains(ourHill)
    }


    public boolean isNear(Ant ant) {
        return getField().getDistance(ant.getPosition(), ourHill) <= 9;
    }
}

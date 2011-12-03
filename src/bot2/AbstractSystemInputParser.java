package bot2;

import bot2.map.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Handles system input stream parsing.
 */
public abstract class AbstractSystemInputParser extends AbstractSystemInputReader {
    private static final String READY = "ready";

    private static final String GO = "go";

    private static final char COMMENT_CHAR = '#';

    private final List<String> input = new ArrayList<String>();

    private enum SetupToken {
        LOADTIME, TURNTIME, ROWS, COLS, TURNS, VIEWRADIUS2, ATTACKRADIUS2, SPAWNRADIUS2;

        private static final Pattern PATTERN = compilePattern(SetupToken.class);
    }

    private enum UpdateToken {
        W, A, F, D, H;

        private static final Pattern PATTERN = compilePattern(UpdateToken.class);
    }

    private static Pattern compilePattern(Class<? extends Enum> clazz) {
        StringBuilder builder = new StringBuilder("(");
        for (Enum enumConstant : clazz.getEnumConstants()) {
            if (enumConstant.ordinal() > 0) {
                builder.append("|");
            }
            builder.append(enumConstant.name());
        }
        builder.append(")");
        return Pattern.compile(builder.toString());
    }

    /**
     * Collects lines read from system input stream until a keyword appears and then parses them.
     */
    @Override
    public void processLine(String line) {
        try {
            if (line.equals(READY)) {
                parseSetup(input);
                doTurn().print(System.out);
                finishTurn();
                input.clear();
            } else if (line.equals(GO)) {
                parseUpdate(input);
                doTurn().print(System.out);
                finishTurn();
                input.clear();
            } else if (!line.isEmpty()) {
                input.add(line);
            }
        }
        catch (Throwable e) {
            System.err.println("Error in process line");
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Parses the setup information from system input stream.
     *
     * @param input setup information
     */
    public void parseSetup(List<String> input) {
        int loadTime = 0;
        int turnTime = 0;
        int rows = 0;
        int cols = 0;
        int turns = 0;
        int viewRadius2 = 0;
        int attackRadius2 = 0;
        int spawnRadius2 = 0;
        for (String line : input) {
            line = removeComment(line);
            if (line.isEmpty()) {
                continue;
            }
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }
            String token = scanner.next().toUpperCase();
            if (!SetupToken.PATTERN.matcher(token).matches()) {
                continue;
            }
            SetupToken setupToken = SetupToken.valueOf(token);
            switch (setupToken) {
                case LOADTIME:
                    loadTime = scanner.nextInt();
                    break;
                case TURNTIME:
                    turnTime = scanner.nextInt();
                    break;
                case ROWS:
                    rows = scanner.nextInt();
                    break;
                case COLS:
                    cols = scanner.nextInt();
                    break;
                case TURNS:
                    turns = scanner.nextInt();
                    break;
                case VIEWRADIUS2:
                    viewRadius2 = scanner.nextInt();
                    break;
                case ATTACKRADIUS2:
                    attackRadius2 = scanner.nextInt();
                    break;
                case SPAWNRADIUS2:
                    spawnRadius2 = scanner.nextInt();
                    break;
            }
        }
        setup(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2);
    }

    /**
     * Parses the update information from system input stream.
     *
     * @param input update information
     */
    public void parseUpdate(List<String> input) {
        beforeUpdate();
        for (String line : input) {
            line = removeComment(line);
            if (line.isEmpty()) {
                continue;
            }
            Scanner scanner = new Scanner(line);
            if (!scanner.hasNext()) {
                continue;
            }
            String token = scanner.next().toUpperCase();
            if (!UpdateToken.PATTERN.matcher(token).matches()) {
                continue;
            }
            UpdateToken updateToken = UpdateToken.valueOf(token);
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            switch (updateToken) {
                case W:
                    updateVisible(col, row, Item.WATER);
                    break;
                case A:
                    if (scanner.hasNextInt()) {
                        int side = scanner.nextInt();
                        updateVisible(col, row, side == 0 ? Item.ANT : Item.ENEMY_ANT, side);
                    }
                    break;
                case F:
                    updateVisible(col, row, Item.FOOD);
                    break;
                case D:
                    if (scanner.hasNextInt()) {
                        int side = scanner.nextInt();
                        //ignore
                        //updateVisible(col, row, side == 0 ? Item.ANT : Item.ENEMY_ANT, side);
                    }
                    break;
                case H:
                    if (scanner.hasNextInt()) {
                        int side = scanner.nextInt();
                        updateVisibleHill(col, row, side);
                    }
                    break;
            }
        }
        afterUpdate();
    }

    protected abstract void updateVisible(int x, int y, Item item);

    protected abstract void updateVisible(int x, int y, Item item, int side);

    protected abstract void updateVisibleHill(int x, int y, int side);

    /**
     * Sets up the game state.
     *
     * @param loadTime timeout for initializing and setting up the bot on turn 0
     * @param turnTime timeout for a single game turn, starting with turn 1
     * @param rows game map height
     * @param cols game map width
     * @param turns maximum number of turns the game will be played
     * @param viewRadius2 squared view radius of each ant
     * @param attackRadius2 squared attack radius of each ant
     * @param spawnRadius2 squared spawn radius of each ant
     */
    public abstract void setup(int loadTime, int turnTime, int rows, int cols, int turns,
                               int viewRadius2, int attackRadius2, int spawnRadius2);

    /**
     * Enables performing actions which should take place prior to updating the game state, like
     * clearing old game data.
     */
    public abstract void beforeUpdate();


    /**
     * Enables performing actions which should take place just after the game state has been
     * updated.
     */
    public abstract void afterUpdate();

    /**
     * Subclasses are supposed to use this method to process the game state and send orders.
     */
    public abstract Orders doTurn();

    /**
     * Finishes turn.
     */
    public void finishTurn() {
        System.out.println("go");
        System.out.flush();
    }

    private String removeComment(String line) {
        int commentCharIndex = line.indexOf(COMMENT_CHAR);
        String lineWithoutComment;
        if (commentCharIndex >= 0) {
            lineWithoutComment = line.substring(0, commentCharIndex).trim();
        } else {
            lineWithoutComment = line;
        }
        return lineWithoutComment;
    }
}

package bot.brain;

import bot.Tile;

public interface AntOrder {
    void step();

    boolean isBusy();

    boolean isHarvesting();

    boolean isFeasible();

    Tile getTarget();

    boolean isRushing();
}

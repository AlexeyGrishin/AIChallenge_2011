package pathfinder;

import bot.Aim;
import bot.Ants;
import bot.Ilk;
import bot.Tile;

import java.util.Arrays;

public class AntsHelper implements PointHelper<Tile> {

    private Ants ants;

    public AntsHelper(Ants ants) {
        this.ants = ants;
    }

    public int getCost(Tile coord) {
        return 1;
    }

    public boolean exists(Tile coord) {
        return true;
    }

    public int getDefaultCost() {
        return 1;
    }

    public boolean isReachable(Tile coord) {
        Ilk ilk = ants.getIlk(coord);
        return ilk.isPassable() && ilk != Ilk.MY_ANT;
    }

    public Iterable<? extends Tile> getNearestCells(Tile coord) {
        return Arrays.asList(
                ants.getTile(coord, Aim.NORTH),
                ants.getTile(coord, Aim.WEST),
                ants.getTile(coord, Aim.SOUTH),
                ants.getTile(coord, Aim.EAST)
        );
    }

    public int getQuickDistanceBetween(Tile source, Tile target) {
        int rowsDiff = Math.abs(target.getRow() - source.getRow());
        int colsDiff = Math.abs(target.getCol() - source.getCol());
        if (rowsDiff > ants.getRows()/2) rowsDiff = ants.getRows() - rowsDiff;
        if (colsDiff > ants.getCols()/2) colsDiff = ants.getCols() - colsDiff;

        return rowsDiff + colsDiff;
    }

    public double getDistanceBetween(Tile source, Tile target) {
        return MathCache.sqrt(ants.getDistance(source, target));
    }
}

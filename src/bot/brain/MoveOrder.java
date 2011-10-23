package bot.brain;

import bot.*;
import pathfinder.AntsHelper;
import pathfinder.PathFinder;

import java.util.LinkedList;

public abstract class MoveOrder implements AntOrder {
    private Tile target;
    private LinkedList<PathFinder<Tile>.PathElement<Tile>> path = null;
    private Ant ant;
    private Ants ants;

    protected MoveOrder(Ants ants, Ant ant, Tile target) {
        this.ants = ants;
        this.ant = ant;
        this.target = target;
        findPath();
    }

    protected void findPath() {
        PathFinder<Tile> finder = new PathFinder<Tile>(new AntsHelper(ants), ant.getPosition(), target);
        finder.setNotFoundStrategy(finder.limited(150, finder.RETURN_PATH_TO_NEAREST));
        finder.findPath();
        this.path = new LinkedList<PathFinder<Tile>.PathElement<Tile>>(finder.getFoundPath());
        if (finder.getObservedCells() > 20) {
            ants.log(ant + ": observed " + finder.getObservedCells() + " cells for " + finder.getSpentTime() + "ms!");
        }
        if (isFeasible()) {
            ants.log(ant + ": get order " + this + ", there are " + path.size() + " steps left");
        }
        else {
            ants.log(ant + ": get order " + this + ", but it is not feaseable =(");
        }
    }

    public void step() {
        ants.log(ant + ": " + this + ": still go to " + target + ", " + path.size() + " steps left");
        PathFinder<Tile>.PathElement<Tile> element = path.removeFirst();
        if (!element.from.equals(ant.getPosition())) {
            ants.log(ant + ": expected to be in " + element.from + ", but I'm in " + ant.getPosition());
            cancel();
            return;
        }
        Aim direction = ants.getDirections(ant.getPosition(), element.to).get(0);
        if (!ant.move(direction)) {
            onMovementImpossible();
            return;
        }
        if (isTargetReached() || isDone()) {
            done();
        }
    }

    protected void onMovementImpossible() {
        cancel();
    }

    protected abstract boolean isDone();

    protected boolean isTargetReached() {
        return path.isEmpty();
    }

    private void done() {
        this.path.clear();
        ants.log(ant + ": " + this + " done");
        ant.done();
    }

    protected void cancel() {
        this.path.clear();
        ants.log(ant + ": " + this + " canceled");
        ant.done();
    }

    public boolean isBusy() {
        return true;
    }

    public boolean isFeasible() {
        return !path.isEmpty();
    }

    public Tile getTarget() {
        return target;
    }

    public boolean isRushing() {
        return false;
    }

    protected Ilk getTargetIlk() {
        return ants.getIlk(target);
    }


    public String toString() {
        return getClass().getSimpleName() + "(" + getTarget() + ")";
    }

}

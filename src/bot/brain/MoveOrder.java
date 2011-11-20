package bot.brain;

import bot.*;
import pathfinder.AntsHelper;
import pathfinder.PathFinder;

import java.util.LinkedList;

public abstract class MoveOrder implements AntOrder {
    private Tile target;
    private LinkedList<PathFinder.PathElement<Tile>> path = null;
    private LinkedList<PathFinder.PathElement<Tile>> madePath = new LinkedList<PathFinder.PathElement<Tile>>();
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
        finder.setNotFoundStrategy(finder.limited(ants.getViewArea(), finder.RETURN_PATH_TO_NEAREST));
        finder.findPath();
        this.path = new LinkedList<PathFinder.PathElement<Tile>>(finder.getFoundPath());
        if (finder.getObservedCells() > 20) {
            ants.log(ant + ": observed " + finder.getObservedCells() + " cells for " + finder.getSpentTime() + "ms!");
        }
        if (isFeasible()) {
            Tile newTarget = path.get(path.size()-1).to;
            if (isNormalDistanceFromTarget(ants.getDistance(target, newTarget))) {
                ants.log(ant + ": get order " + this + ", there are " + path.size() + " steps left");
            }
            else {
                ants.log(ant + ": get order " + this + ", but path was found to " + newTarget + " only, which is not acceptable");
                path.clear();
            }
        }
        else {
            ants.log(ant + ": get order " + this + ", but it is not feaseable =(");
        }
    }

    protected void removeLastStep() {
        path.removeLast();
    }

    protected PathFinder.PathElement<Tile> getLastStep() {
        return path.isEmpty() ? new PathFinder.PathElement<Tile>(ant.getPosition(), ant.getPosition(), 0, 0) : path.getLast();
    }

    public void step() {
        ants.log(ant + ": " + this + ": still go to " + target + ", " + path.size() + " steps left");
        PathFinder.PathElement<Tile> element = path.removeFirst();
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
        madePath.add(element);
        if (isTargetReached() || isDone()) {
            done();
        }
    }

    public void stepBack() {
        if (!madePath.isEmpty()) {
            PathFinder.PathElement<Tile> lastStep = madePath.removeLast();
            ant.move(ants.getDirections(lastStep.to, lastStep.from).get(0));
            ant.setLastDirection(ants.getDirections(lastStep.from, lastStep.to).get(0));
            path.addFirst(lastStep);
            ants.log(ant + ": " + this + ": still go to " + target + ", " + path.size() + " steps left, step back");
        }
    }

    protected boolean isNormalDistanceFromTarget(int distance2) {
        return true;
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

    protected void log(String s) {
        ants.log(ant + ": " + this + ": " + s);
    }

    protected Ant getAnt() {
        return ant;
    }

}

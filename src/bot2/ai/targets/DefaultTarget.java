package bot2.ai.targets;

import bot2.Logger;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.View;
import pathfinder.PathFinder;
import pathfinder.PointHelper;

import java.util.LinkedList;

public abstract class DefaultTarget implements Target {

    private View view;
    private FieldPoint target;
    private LinkedList<PathFinder.PathElement<FieldPoint>> path;
    private PointHelper<FieldPoint> helper;
    private boolean unreachable = false;
    private PathFinder.PathElement<FieldPoint> lastStep;

    public DefaultTarget(FieldPoint target, View view) {
        this.target = target;
        this.helper = view.producePointHelper();
        this.view = view;
    }

    private void initPath(FieldPoint from) {
        PathFinder<FieldPoint> finder = new PathFinder<FieldPoint>(this.helper, from, target);
        finder.findPath();
        this.path = new LinkedList<PathFinder.PathElement<FieldPoint>>(finder.getFoundPath());
    }

    public boolean isReached(FieldPoint location) {
        return unreachable
                || isTargetFound(location);
    }

    protected abstract boolean isTargetFound(FieldPoint location);

    public FieldPoint nextStep(FieldPoint location) {
        if (!hasPath()) {
            initPath(location);
        }
        if (hasPath()) {
            PathFinder.PathElement<FieldPoint> next = path.removeFirst();
            lastStep = next;
            if (next.from.equals(location)) {
                return next.to;
            }
            else {
                path = null;
                if (shallContinueOnPathProblems(location, next.from))
                    return nextStep(location);
            }
        }
        //very strange, probably unreachable
        path = null;
        unreachable = true;
        Logger.log("Target " + target + " seems to be unreachable from " + location + ", mark finished as unreachable");
        return null;
    }

    public FieldPoint predictNextStep() {
        return hasPath() ? path.get(0).to : null;
    }

    public void restart() {
        path = null;
    }

    public void stepBack() {
        if (lastStep != null && hasPath()) {
            path.addFirst(lastStep);
            lastStep = null;
        }

    }

    protected boolean shallContinueOnPathProblems(FieldPoint location, FieldPoint expected) {
        return true;
    }

    private boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    protected int getQuickDistanceToTarget(FieldPoint location) {
        return helper.getQuickDistanceBetween(location, getTarget());
    }

    protected boolean seeTarget() {
        return view.see(target);
    }

    protected Item getTargetItem() {
        return view.getItem(getTarget());
    }

    public FieldPoint getTarget() {
        return target;
    }

    public String toString() {
        return getClass().getSimpleName() + ": " + target + ", " + (hasPath() ? "path remaining" + path : "no path") + ", " + (unreachable ? "unreachable": "reachable");
    }
}

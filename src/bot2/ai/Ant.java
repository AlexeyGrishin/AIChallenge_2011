package bot2.ai;

import bot2.Logger;
import bot2.MoveHelper;
import bot2.ai.areas.FieldArea;
import bot2.ai.targets.*;
import bot2.map.Direction;
import bot2.map.FieldPoint;
import bot2.map.View;

public class Ant {

    private int nr;
    private static int NR = 1;
    enum IfStepFailed {
        REPEAT,
        CANCEL
    }

    private FieldPoint location;
    private Target nextTarget;
    private MoveHelper mover;
    private boolean moved = false;
    private View visibleView;
    private FieldArea targetArea;

    public Ant(FieldPoint location, MoveHelper mover, View visibleView) {
        this.location = location;
        this.mover = mover;
        this.visibleView = visibleView;
        this.nr = NR++;
        log(" =)");
    }

    public boolean move(FieldPoint target) {
        if (target == null) {
            return false;
        }
        if (!moved && mover.canMoveTo(target)) {
            FieldPoint oldLocation = location;
            location = mover.move(oldLocation, target);
            moved = !oldLocation.equals(location);
            return moved;
        }
        return false;
    }

    public void beforeTurn() {
        moved = false;
        visibleView.setPoint(location);
    }

    public FieldPoint getLocation() {
        return location;
    }

    public View getVisibleView() {
        return visibleView;
    }

    public void die() {
        // =(
        log(" =(");
    }

    public void goToPoint(FieldPoint point) {
        log(location + ": Go to point " + point);
        nextTarget = new PointTarget(point, visibleView);
        update();
    }

    public void doReachArea(FieldArea area) {
        log(location + ": Go to area " + area.getNumber() + " at " + area.getCenter());
        targetArea = area;
        FieldArea nextArea = mover.getNextAreaOnWayTo(location, targetArea);
        log(location + ": Next area on my way is " + nextArea.getNumber() + " at " + area.getCenter());
        nextTarget =
                nextArea.isReached()
                        ? new AreaTarget(nextArea, visibleView)
                        : new NotReachedArea(nextArea, visibleView);
        update();
    }

    public void doGatherFood(FieldPoint point) {
        log(location + ": Gather food at " + point);
        targetArea = null;
        nextTarget = new FoodTarget(point, visibleView);
        update();
    }

    private void log(String str) {
        Logger.log(this + ": " + str);
    }

    public void update() {
        log("Perform target: " + nextTarget);
        doStep(IfStepFailed.REPEAT);
    }

    private void doStep(IfStepFailed ifFailed) {
        if (nextTarget != null && !nextTarget.isReached(location) && !moved) {
            boolean result = move(nextTarget.getNestStep(location));
            if (!result && ifFailed == IfStepFailed.REPEAT) {
                nextTarget.restart();
                doStep(IfStepFailed.CANCEL);
                return;
            }
        }
        if (nextTarget != null && nextTarget.isReached(location)) {
            nextTarget = null;
            targetArea = null;
            //done
        }

    }

    public boolean isBusy() {
        return nextTarget != null;
    }

    public FieldPoint getTargetPoint() {
        return nextTarget != null ? nextTarget.getTarget() : location;
    }

    public boolean isHarvesting() {
        return nextTarget instanceof FoodTarget;
    }

    public String toString() {
        return "Ant#" + nr + "(" + location + ")";
    }

    //for debug only
    public String getTargetName() {
        return isBusy() ? nextTarget.getClass().getSimpleName() : "<Nothing>";
    }

    public FieldArea getTargetArea() {
        return targetArea;
    }
}

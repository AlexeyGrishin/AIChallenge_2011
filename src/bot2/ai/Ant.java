package bot2.ai;

import bot2.Logger;
import bot2.MoveHelper;
import bot2.ai.areas.AreasMapper;
import bot2.ai.areas.FieldArea;
import bot2.ai.targets.*;
import bot2.map.FieldPoint;
import bot2.map.View;

public class Ant {

    private int nr;
    private static int NR = 1;
    private boolean inUpdate = false;
    private int skipSteps = 0;

    public int getNr() {
        return nr;
    }

    public boolean isSkippingStep() {
        return skipSteps > 0;
    }

    enum IfStepFailed {
        KICK_OR_REPEAT,
        REPEAT,
        CANCEL;

        public boolean canKick() {
            return this == IfStepFailed.KICK_OR_REPEAT;
        }

        public boolean canRepeat() {
            return this == REPEAT || this == KICK_OR_REPEAT;
        }
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
        if (skipSteps > 0) skipSteps--;
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

    public void doAttackHill(FieldPoint point, HillsHelper helper) {
        log(": Attack hill at " + point + "!");
        nextTarget = new AttackHill(point, visibleView, helper);
        update();
    }

    public void doWalkToPoint(FieldPoint point) {
        log( ": Go to point " + point);
        nextTarget = new PointTarget(point, visibleView);
        update();
    }

    public void doExitUnknownArea(FieldArea area, AreasMapper mapper) {
        if (checkNullArea(area)) return;
        log( ": Go to area " + area.getNumber() + " at " + area.getCenter() + " just to move out from unknown points");
        targetArea = area;
        nextTarget = new FindKnownArea(area, visibleView, mapper);
        update();
    }

    private boolean checkNullArea(FieldArea area) {
        if (area == null) {
            log("Was sent to null area. I will just wait here");
            return true;
        }
        return false;
    }

    public void doReachArea(FieldArea area) {
        if (checkNullArea(area)) return;
        log( ": Go to area " + area.getNumber() + " at " + area.getCenter());
        targetArea = area;
        FieldArea nextArea = mover.getNextAreaOnWayTo(location, targetArea);
        if (nextArea != null) {
            log(location + ": Next area on my way is " + nextArea.getNumber() + " at " + nextArea.getCenter());
            nextTarget =
                    nextArea.isReached()
                            ? new AreaTarget(nextArea, visibleView)
                            : new NotReachedArea(nextArea, visibleView);
            update();
        }
    }

    public void doGatherFood(FieldPoint point) {
        log(": Gather food at " + point);
        targetArea = null;
        nextTarget = new FoodTarget(point, visibleView);
        update();
    }

    private void log(String str) {
        Logger.log(this + ": " + str);
    }

    public void update() {
        if (inUpdate) {
            log("Already in update");
            return;
        }
        inUpdate = true;
        try {
            doStep(IfStepFailed.KICK_OR_REPEAT);
        }
        finally {
            inUpdate = false;
        }
    }

    private void doStep(IfStepFailed ifFailed) {
        if (nextTarget != null && !nextTarget.isReached(location) && !moved) {
            log("Perform target: " + nextTarget);
            if (isSkippingStep()) {
                log(" Skip step");
            }
            FieldPoint nextStep = nextTarget.getNestStep(location);
            boolean nextStepExists = nextStep != null;
            boolean result = nextStepExists && move(nextStep);
            if (!result && nextStepExists && ifFailed.canKick()) {
                if (tryToKick(nextStep)) return;
            }
            if (!result && mover.isItemMoving(nextStep)) {
                skipSteps = 2;
                log("  Skip step");
                return;
            }
            if (!result && ifFailed.canRepeat()) {
                tryRepeat();
                return;
            }
        }
        if (nextTarget != null && nextTarget.isReached(location)) {
            nextTarget = null;
            targetArea = null;
            //done
        }

    }

    private void tryRepeat() {
        nextTarget.restart();
        doStep(IfStepFailed.CANCEL);
    }

    private boolean tryToKick(FieldPoint nextStep) {
        boolean wasMovedOut = mover.kickOurAntAt(nextStep);
        if (wasMovedOut) {
            Logger.log(" kicked out!");
            doStep(IfStepFailed.REPEAT);
            return true;
        }
        return false;
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

    public boolean isAttackingHill() {
        return nextTarget instanceof AttackHill;
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

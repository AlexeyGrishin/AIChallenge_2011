package bot2;

import bot2.ai.areas.FieldArea;
import bot2.map.Direction;
import bot2.map.FieldPoint;

public interface MoveHelper {

    /**
     * Moves ant on oldLocation to target, assuming that the points are near
     * @param oldLocation current ant location
     * @param target target point
     * @return the result point - target if move succeeded, oldLocation - if failed
     */
    FieldPoint move(FieldPoint oldLocation, FieldPoint target);

    boolean canMoveTo(FieldPoint target);

    FieldArea getNextAreaOnWayTo(FieldPoint from, FieldArea targetArea);

    /**
     * Calls ant on the point to perform the movement.
     * @param point
     * @return true if ant was moved out, false - if not, or if there is no ant at the point
     */
    boolean kickOurAntAt(FieldPoint point);

    boolean isItemMoving(FieldPoint point);
}

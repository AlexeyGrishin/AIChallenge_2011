package bot2.ai.targets;

import bot2.map.Direction;
import bot2.map.FieldPoint;

public interface Target {

    boolean isReached(FieldPoint location);

    FieldPoint getNestStep(FieldPoint location);

    FieldPoint getTarget();

    void restart();
}

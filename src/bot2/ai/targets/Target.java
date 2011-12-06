package bot2.ai.targets;

import bot2.map.Direction;
import bot2.map.FieldPoint;

public interface Target {

    boolean isReached(FieldPoint location);

    FieldPoint nextStep(FieldPoint location);

    FieldPoint predictNextStep();

    FieldPoint getTarget();

    void restart();

    void stepBack();
}

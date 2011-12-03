package bot2.ai.targets;

import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.View;
import pathfinder.PointHelper;



public class FoodTarget extends DefaultTarget {

    public FoodTarget(FieldPoint target, View field) {
        super(target, field);
    }

    @Override
    protected boolean isTargetFound(FieldPoint location) {
        return getTargetItem() != Item.FOOD
                || getQuickDistanceToTarget(location) <= 1;
    }



}

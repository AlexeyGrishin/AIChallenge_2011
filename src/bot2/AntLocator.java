package bot2;

import bot2.ai.Ant;
import bot2.map.FieldPoint;

public interface AntLocator {

    public Ant getAnt(FieldPoint point);
}

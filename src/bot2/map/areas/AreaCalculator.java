package bot2.map.areas;

import java.util.List;

public interface AreaCalculator<K> {

    public List<ViewPoint> calculate(K key);
}

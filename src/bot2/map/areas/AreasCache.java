package bot2.map.areas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreasCache<K> {

    private Map<K, List<ViewPoint>> view = new HashMap<K, List<ViewPoint>>();
    private AreaCalculator<K> calculator;

    public AreasCache(AreaCalculator<K> calculator) {
        this.calculator = calculator;
    }

    public List<ViewPoint> getPoints(K key) {
        List<ViewPoint> points = view.get(key);
        if (points == null) {
            points = this.calculator.calculate(key);
            view.put(key, points);
        }
        return points;
    }

}

package pathfinder;

import java.util.HashMap;
import java.util.Map;

public class MathCache<K extends Number, V extends Number> {

    private Map<K, V> cache = new HashMap<K, V>();

    private final static int QCACHE_SIZE = 100;
    private Number[] positive = new Number[QCACHE_SIZE];
    private Number[] nevative = new Number[QCACHE_SIZE];
    private Number zeroVal = null;

    public interface Calc<K, V> {
        public V calc(K val);
    }

    private Calc<K, V> calc;

    public MathCache(Calc<K, V> calc) {
        this.calc = calc;
    }


    public V calculate(K val) {
        long v = val.longValue();
        V res = getFromCache(v);
        if (res == null) {
            res = calc.calc(val);
            putToCache(val, v, res);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private V getFromCache(long v) {
        if (v == 0) return (V) zeroVal;
        if (v > 0 && v < 100) return (V) positive[(int) v];
        if (v < 0 && v > -100) return (V) nevative[(int)-v];
        return cache.get(v);
    }

    private void putToCache(K v, long val, V res) {
        if (val == 0) {
            zeroVal = res;
        }
        else if (val > 0 && val < 100) {
            positive[(int) val] = res;
        }
        else if (val < 0 && val > -100) {
            nevative[(int) -val] = res;
        }
        else {
            cache.put(v, res);
        }
    }

    private static MathCache<Long, Long> longSquares = new MathCache<Long, Long>(new Calc<Long, Long>() {
        public Long calc(Long val) {
            return val * val;
        }
    });
    private static MathCache<Long, Double> longSqrts = new MathCache<Long, Double>(new Calc<Long, Double>() {
        public Double calc(Long val) {
            return Math.sqrt(val);
        }
    });

    public static long square(long val) {
        return longSquares.calculate(val);
    }

    public static double sqrt(long val) {
        return longSqrts.calculate(val);
    }
}

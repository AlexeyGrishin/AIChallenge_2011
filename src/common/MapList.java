package common;

import java.util.*;

public class MapList<K, V> {

    private Map<K, List<V>> map = new HashMap<K, List<V>>();

    public void put(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(key, list);
        }
        list.add(value);
    }

    public List<V> get(K key) {
        List<V> list = map.get(key);
        if (list == null) list = Collections.emptyList();
        return list;
    }

    public Set<K> getKeys() {
        return map.keySet();
    }

    public void remove(K key) {
        map.remove(key);
    }
}

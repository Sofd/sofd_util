package de.sofd.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * BiMap implementation with O(1) time complexity in both directions
 */
public class BiIdentityHashMap<K,V> extends IdentityHashMap<K,V> implements BiMap<K,V> {
    //TODO: deriving from HashMap is unclean ("fragile base class"),
    //   but quickly done

    private final Map<V,K> reverseMap = new IdentityHashMap<V,K>();

    /**
     * @see de.fhg.isst.wind.supportgui.util.BiMap#reverseGet(java.lang.Object)
     */
    @Override
    public K reverseGet(V value) {
        return reverseMap.get(value);
    }

    /**
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        super.clear();
        reverseMap.clear();
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return reverseMap.containsKey(value);
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(K key, V value) {
        reverseMap.put(value,key);
        return super.put(key, value);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> e: map.entrySet()) {
            put(e.getKey(),e.getValue());
        }
    }

    /**
     * @see java.util.Map#remove(Object)
     */
    @Override
    public V remove(Object key) {
        reverseMap.remove(get(key));
        return super.remove(key);
    }

    /**
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values() {
        return reverseMap.keySet();
    }

}

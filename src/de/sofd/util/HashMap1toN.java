package de.sofd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map1toN implementation that internally uses a HashMap and an ArrayList for
 * the collections (override
 * {@link #createValuesCollectionFor(Object) createValuesCollectionFor} to
 * change the latter).
 * 
 * @author Olaf Klischat
 * 
 * @param <K>
 * @param <V>
 */
public class HashMap1toN<K,V> implements Map1toN<K,V> {

    private final Map<K, Collection<V>> backend = new HashMap<K, Collection<V>>();
    
    @Override
    public void put(K k, V v) {
        Collection<V> vs = backend.get(k);
        if (null == vs) {
            vs = createValuesCollectionFor(k);
            backend.put(k, vs);
        }
        vs.add(v);
    }
    
    @Override
    public Collection<V> get(K k) {
        Collection<V> vs = backend.get(k);
        if (null == vs) {
            return createValuesCollectionFor(k);
        }
        Collection<V> result = createValuesCollectionFor(k);
        result.addAll(vs);  // return a copy instead of a Collections.unmodifiablyCollection so the user
                            // may iterate over the result while modifying the collection for k
        return result;
    }
    
    @Override
    public boolean contains(K k, V v) {
        return get(k).contains(v);
    }
    
    @Override
    public Set<K> keySet() {
        return backend.keySet();
    }
    
    @Override
    public void remove(K k, V v) {
        Collection<V> vs = backend.get(k);
        if (vs != null) {
            vs.remove(v);
            if (vs.isEmpty()) {
                backend.remove(k);
            }
        }
    }
    
    @Override
    public void removeAll(K k) {
        backend.remove(k);
    }

    @Override
    public void clear() {
        backend.clear();
    }
    
    protected Collection<V> createValuesCollectionFor(K k) {
        return new ArrayList<V>();
    }
}

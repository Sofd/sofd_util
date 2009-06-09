package de.sofd.util;

import java.util.Collection;
import java.util.Set;

/**
 * Map that maps keys to collections of values.
 * <p>
 * Essentially a Map&lt;K,Collection&lt;V&gt;&gt; that automatically takes care
 * of adding/removing the collections and their members as necessary.
 * 
 * @author Olaf Klischat
 * 
 * @param <K>
 * @param <V>
 */
public interface Map1toN<K,V> {
    /**
     * Add a new value (v) to the collection of values for k
     * 
     * @param k
     *            k
     * @param v
     *            v
     */
    void put(K k, V v);
    /**
     * 
     * @param k
     *            key
     * @return collection of values for k. Empty collection if no values are
     *         there. Never null.
     */
    Collection<V> get(K k);
    /**
     * 
     * @param k
     *            k
     * @param v
     *            v
     * @return does k's collection contain v?
     */
    boolean contains(K k, V v);
    Set<K> keySet();
    void remove(K k, V v);
    void removeAll(K k);
    void clear();
}

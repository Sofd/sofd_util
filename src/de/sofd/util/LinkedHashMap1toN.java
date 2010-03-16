package de.sofd.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Like {@link HashMap1toN}, but keeps its keys in insertion order.
 * 
 * @author Olaf Klischat
 * 
 * @param <K>
 * @param <V>
 */
public class LinkedHashMap1toN<K, V> extends HashMap1toN<K, V> {

    @Override
    protected Map<K, Collection<V>> createBackingMap() {
        return new LinkedHashMap<K, Collection<V>>();
    }

}

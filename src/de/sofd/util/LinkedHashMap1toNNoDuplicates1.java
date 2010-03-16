package de.sofd.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Combination of {@link LinkedHashMap1toN} and {@link HashMap1toNNoDuplicates}.
 * 
 * @author Olaf Klischat
 * 
 * @param <K>
 * @param <V>
 */
public class LinkedHashMap1toNNoDuplicates1<K, V> extends HashMap1toN<K, V> {

    @Override
    protected Map<K, Collection<V>> createBackingMap() {
        return new LinkedHashMap<K, Collection<V>>();
    }

    @Override
    protected Collection<V> createValuesCollectionFor(K k) {
        return new HashSet<V>();
    }
}

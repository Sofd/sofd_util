package de.sofd.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * Like {@link HashMap1toN}, but does not permit duplicates in its values
 * collections.
 * 
 * @author Olaf Klischat
 * 
 * @param <K>
 * @param <V>
 */
public class HashMap1toNNoDuplicates<K, V> extends HashMap1toN<K, V> {

    @Override
    protected Collection<V> createValuesCollectionFor(K k) {
        return new HashSet<V>();
    }
}

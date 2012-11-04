package de.sofd.util;

import java.util.Map;

/**
 * Map that also maps backward (value->key).
 */
public interface BiMap<K,V> extends Map<K,V> {
    /**
     * TODO_Comment
     * @param value TODO_Comment
     * @return TODO_Comment
     */
    K reverseGet(V value);
}

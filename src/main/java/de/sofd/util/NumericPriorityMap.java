package de.sofd.util;

import de.sofd.lang.Function1;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Base interface for a map with per-key priority and cost values.
 * <p>
 * Basically a Map that has a priority (real number) associated with
 * each of its keys. The priorities govern iteration order and eviction policy.
 * The priority of an element is an explicitly assigned real number (double),
 * set when the element is added to the map. This is in contrast to {@link TreeMap}
 * and friends, where the priorities of the elements relative to each other
 * are determined by their {@link Comparable natural order}. Using explicit
 * numbers for priorities allows us to implement all basic operations of the
 * map (get(), put() etc.) with O(1) time complexity, rather than O(log n)
 * for TreeMap (see the implementation class {@link BucketedNumericPriorityMap}
 * for details).
 * <p>
 * In addition to the priority values, each element also has an associated
 * <em>cost</em> value (also a real number). When the total cost of the
 * elements (= number of elements in the simplest case) exceeds a configurable
 * limit ( {@link #getMaxTotalCost()}), other elements will be thrown out
 * (evicted), starting with the elements with the lowest priorities. Setting
 * the limit to a value less than 0 disables it (i.e. the total cost can grow
 * unboundedly).
 * <p>
 * To "cost" of an element in the map is determined by the
 * "element cost function" {@link #getElementCostFunction()}, which maps each
 * element to its cost, a non-negative number. This is commonly used for things
 * like the effective "memory consumption" of the element -- so then map can
 * contain many small or fewer large elements or anything in between. In that
 * case, {@link #getMaxTotalCost()} will be the maximum total memory consumption
 * up to which the map will grow before it starts evicting low-priority
 * elements. In the default case, the element cost function will just return 1
 * for all elements, so the {@link #getMaxTotalCost()} will effectively be the
 * maximum number of elements in the map. Please note that the cost function's
 * value MUST NOT change for an element as long as it stays in the map.
 * <p>
 * There is a boolean isReverseEviction flag -- if it is true, eviction starts
 * with the highest-priority elements, not the lowest-priority ones.
 * <p>
 * TODO: derive this from java.util.Map (submitting priorities via a function
 * rather than as an additional parameter to put() et.al)
 *
 * @author olaf
 * 
 * @param <K>
 * @param <V>
 */
public interface NumericPriorityMap<K, V> {

    boolean isReverseEviction();

    void setReverseEviction(boolean reverseEviction);

    public interface Entry<K, V> {
        K getKey();
        V getValue();
        double getPriority();
    }

    boolean contains(K k);

    V get(K k);

    double getCurrentTotalCost();

    Function1<V, Double> getElementCostFunction();

    double getMaxTotalCost();

    boolean isEmpty();

    V put(K k, V v, double priority);

    V remove(K k);

    void setMaxTotalCost(double maxTotalCost);

    /**
     * Doesn't do anything if k isn't currently stored. The caller should be
     * aware of that if needed.
     */
    void setPriority(K k, double priority);

    int size();

    Iterator<Entry<K, V>> entryIterator();

    public Iterator<Entry<K,V>> reverseEntryIterator();

}

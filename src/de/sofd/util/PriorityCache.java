package de.sofd.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.sofd.lang.Function1;

/**
 * A priority-based cache. Basically a Map that has a priority (real number)
 * associated with each of its keys. The priorities govern iteration order and
 * eviction policy. When the "total cost" of the elements (= number of elements
 * in the simplest case) exceeds a configurable limit (
 * {@link #getMaxTotalCost()}), other elements will be thrown out (evicted),
 * starting with the elements with the lowest priorities.
 * <p>
 * To "cost" of an element in the cache is determined by the
 * "element cost function" {@link #getElementCostFunction()}, which maps each
 * element to its cost, a non-negative integer. This is commonly used for things
 * like the effective "memory consumption" of the element -- so then cache can
 * contain many small os fewer large elements or anything in between. In that
 * case, {@link #getMaxTotalCost()} will be the maximum total memory consumption
 * up to which the cache will grow before it starts evicting low-priority
 * elements. In the default case, the element cost function will just return 1
 * for all element, so the {@link #getMaxTotalCost()} will effectively be the
 * maximum number of elements in the cache.
 * <p>
 * The time complexity of all basic operations will generally be O(1) (refer to
 * {@link #PriorityCache(double, double, int, int, Function1)} to see what
 * compromises are made to achieve this).
 * 
 * @author olaf
 * 
 * @param <K>
 * @param <V>
 */
public class PriorityCache<K, V> {

    private final double lowPrio, highPrio;
    private final int nBuckets;
    private LinkedHashMap<K, V>[] buckets;
    private final double bucketWidth;

    /**
     * @invariant priorities.keySet() contains the same elements as \\union_{b
     *            \in buckets} b.keySet()
     */
    private final Map<K, Double> priorities = new HashMap<K, Double>();

    private final Function1<V, Integer> elementCostFunction;

    int totalCost = 0;
    int maxTotalCost;

    /**
     * Creates a default PriorityCache with a 0..10 sensitive priority range and
     * 10 buckets. The maximum total cost will be 1000, the element cost
     * function will be one that always returns 1 -- so the cache will grow up
     * to a maximum of 1000 elements.
     */
    public PriorityCache() {
        this(0, 10, 10, 1000, new Function1<V, Integer>() {
            public Integer run(V p0) {
                return 1;
            }
        });
    }

    /**
     * To provide greater efficiency, elements will internally be collected into
     * a fixed number of "buckets" according to their priority. All elements
     * with priorities below lowPrio go into the "lowest priority" bucket, all
     * elements with priorities above highPrio go into the "highest priority"
     * bucket. The interval between lowPrio and highPrio is divided into
     * nBuckets equal-length areas (buckets), and element with priorities in
     * that range will be put into the corresponding bucket. Inside a bucket,
     * elements won't be sorted by priority anymore (but by access order
     * instead). By making this compromise we can ensure that all operations
     * will have O(1) time complexity (rather than O(log n) for strictly
     * priority-ordered data structures like tree sets).
     * 
     * @param lowPrio
     * @param highPrio
     * @param nBuckets
     * @param maxTotalCost
     * @param elementCostFunction
     */
    @SuppressWarnings("unchecked")
    public PriorityCache(double lowPrio, double highPrio, int nBuckets,
            int maxTotalCost, Function1<V, Integer> elementCostFunction) {
        if (lowPrio >= highPrio || nBuckets <= 0) {
            throw new IllegalArgumentException();
        }
        this.lowPrio = lowPrio;
        this.highPrio = highPrio;
        this.nBuckets = nBuckets;
        this.buckets = new LinkedHashMap[nBuckets];
        for (int i = 0; i < nBuckets; i++) {
            buckets[i] = new LinkedHashMap<K, V>(256, 0.75F, true);
        }
        this.bucketWidth = (highPrio - lowPrio) / nBuckets;
        this.maxTotalCost = maxTotalCost;
        this.elementCostFunction = elementCostFunction;
    }

    public void put(K k, V v, double priority) {
        // TODO
        throw new UnsupportedOperationException();
    }


    public V get(K k) {
        // TODO
        throw new UnsupportedOperationException();
    }
    
    public boolean contains(K k) {
        return priorities.containsKey(k);
    }

    /**
     * Doesn't do anything if k isn't currently stored. The caller
     * should be aware of that if needed.
     */
    public void setPriority(K k, double priority) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int getCurrentTotalCost() {
        return totalCost;
    }

    public int getMaxTotalCost() {
        return maxTotalCost;
    }

    public void setMaxTotalCost(int maxTotalCost) {
        this.maxTotalCost = maxTotalCost;
        evictExcessElements();
    }

    public Function1<V, Integer> getElementCostFunction() {
        return elementCostFunction;
    }

    public void evictExcessElements() {
        // TODO
        throw new UnsupportedOperationException();
    }

}

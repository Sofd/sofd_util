package de.sofd.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.sofd.lang.Function1;

/**
 * PriorityCache implementation that provides O(1) time complexity for all
 * operations (refer to
 * {@link #BucketedPriorityCache(double, double, int, int, Function1)} to see
 * what compromises are made to achieve this).
 * <p>
 * Please note that this class is synchronized.
 * 
 * @author olaf
 * 
 * @param <K>
 * @param <V>
 */
public class BucketedPriorityCache<K, V> implements PriorityCache<K, V> {

    protected class Entry {
        V v;
        double priority;

        public Entry(V v, double priority) {
            super();
            this.v = v;
            this.priority = priority;
        }
    }

    private final Map<K, Entry> entries = new HashMap<K, Entry>();

    private final double lowPrio, highPrio;
    private final int nBuckets, maxBucketNr;
    private LinkedHashMap<K, Entry>[] buckets;
    private final double bucketWidth;

    private final Function1<V, Integer> elementCostFunction;

    int totalCost = 0;
    int maxTotalCost;

    /**
     * Creates a default PriorityCache with a 0..10 sensitive priority range and
     * 10 buckets. The maximum total cost will be 1000, the element cost
     * function will be one that always returns 1 -- so the cache will grow up
     * to a maximum of 1000 elements.
     */
    public BucketedPriorityCache() {
        this(0, 10, 10, 1000, new Function1<V, Integer>() {
            @Override
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
    public BucketedPriorityCache(double lowPrio, double highPrio, int nBuckets,
            int maxTotalCost, Function1<V, Integer> elementCostFunction) {
        if (lowPrio >= highPrio || nBuckets <= 0) {
            throw new IllegalArgumentException();
        }
        this.lowPrio = lowPrio;
        this.highPrio = highPrio;
        this.nBuckets = nBuckets;
        this.maxBucketNr = nBuckets - 1;
        this.buckets = new LinkedHashMap[nBuckets];
        for (int i = 0; i < nBuckets; i++) {
            buckets[i] = new LinkedHashMap<K, Entry>(256, 0.75F, true);
        }
        this.bucketWidth = (highPrio - lowPrio) / nBuckets;
        this.maxTotalCost = maxTotalCost;
        this.elementCostFunction = elementCostFunction;
    }

    protected int prio2bucketNr(double prio) {
        return Math.max(0, Math.min(maxBucketNr,
                (int) ((prio - lowPrio) / bucketWidth)));
    }

    @Override
    public synchronized V put(K k, V v, double priority) {
        V result = null;
        Entry newE = new Entry(v, priority);
        Entry oldE = entries.put(k, newE);
        if (null != oldE) {
            buckets[prio2bucketNr(oldE.priority)].remove(k);
            totalCost -= elementCostFunction.run(oldE.v);
            result = oldE.v;
        }
        buckets[prio2bucketNr(newE.priority)].put(k, newE);
        totalCost += elementCostFunction.run(newE.v);
        evictExcessElements();
        return result;
    }

    @Override
    public synchronized V get(K k) {
        Entry e = entries.get(k);
        if (e == null) {
            return null;
        } else {
            return e.v;
        }
    }

    @Override
    public synchronized V remove(K k) {
        Entry oldE = entries.remove(k);
        if (oldE != null) {
            buckets[prio2bucketNr(oldE.priority)].remove(k);
            totalCost -= elementCostFunction.run(oldE.v);
            return oldE.v;
        } else {
            return null;
        }
    }

    @Override
    public synchronized boolean contains(K k) {
        return entries.containsKey(k);
    }

    @Override
    public synchronized int size() {
        return entries.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Doesn't do anything if k isn't currently stored. The caller should be
     * aware of that if needed.
     */
    @Override
    public synchronized void setPriority(K k, double priority) {
        Entry oldE = entries.remove(k);
        if (oldE != null) {
            buckets[prio2bucketNr(oldE.priority)].remove(k);
            Entry newE = new Entry(oldE.v, priority);
            entries.put(k, newE);
            buckets[prio2bucketNr(newE.priority)].put(k, newE);
        }
    }

    @Override
    public synchronized int getCurrentTotalCost() {
        return totalCost;
    }

    @Override
    public synchronized int getMaxTotalCost() {
        return maxTotalCost;
    }

    @Override
    public synchronized void setMaxTotalCost(int maxTotalCost) {
        if (maxTotalCost < 0) {
            throw new IllegalArgumentException();
        }
        this.maxTotalCost = maxTotalCost;
        evictExcessElements();
    }

    @Override
    public Function1<V, Integer> getElementCostFunction() {
        return elementCostFunction;
    }

    protected void evictExcessElements() {
        while ((totalCost > maxTotalCost) && (entries.size() > 1)) {
            // ^^^ ensure size >= 1 to always keep at least one element in, even
            // if it alone exceeds maxTotalCost
            for (int iBucket = 0; iBucket < nBuckets; iBucket++) {
                LinkedHashMap<K, Entry> bucket = buckets[iBucket];
                if (bucket.isEmpty()) {
                    continue;
                }
                Map.Entry<K, Entry> oldestMapEntry = bucket.entrySet()
                        .iterator().next();
                Entry oldestEntry = oldestMapEntry.getValue();
                totalCost -= elementCostFunction.run(oldestEntry.v);
                bucket.remove(oldestMapEntry.getKey());
                entries.remove(oldestMapEntry.getKey());
                break;
            }
        }
    }

}

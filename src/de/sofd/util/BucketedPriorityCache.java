package de.sofd.util;

import de.sofd.util.PriorityCache.Entry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.sofd.lang.Function1;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * PriorityCache implementation that provides O(1) time complexity for all
 * operations (refer to
 * {@link #BucketedPriorityCache(double, double, int, int, Function1)} to see
 * what compromises are made to achieve this).
 * <p>
 * Please note that this class is synchronized (TODO: undo that and have a
 * separate synchronized wrapper)
 * 
 * @author olaf
 * 
 * @param <K>
 * @param <V>
 */
public class BucketedPriorityCache<K, V> implements PriorityCache<K, V> {

    protected static class EntryImpl<K, V> implements Entry<K,V> {
        K k;
        V v;
        double priority;

        public EntryImpl(K k, V v, double priority) {
            super();
            this.k = k;
            this.v = v;
            this.priority = priority;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public double getPriority() {
            return priority;
        }
    }

    private final Map<K, EntryImpl<K,V>> entries = new HashMap<K, EntryImpl<K,V>>();

    private final double lowPrio, highPrio;
    private final int nBuckets, maxBucketNr;
    private LinkedHashMap<K, EntryImpl<K,V>>[] buckets;
    private final double bucketWidth;

    private final Function1<V, Double> elementCostFunction;

    double totalCost = 0;
    double maxTotalCost;

    /**
     * Creates a default PriorityCache with a 0..10 sensitive priority range and
     * 10 buckets. The maximum total cost will be 1000, the element cost
     * function will be one that always returns 1 -- so the cache will grow up
     * to a maximum of 1000 elements.
     */
    public BucketedPriorityCache() {
        this(0, 10, 10, 1000, null);
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
            double maxTotalCost, Function1<V, Double> elementCostFunction) {
        if (lowPrio >= highPrio || nBuckets <= 0) {
            throw new IllegalArgumentException();
        }
        this.lowPrio = lowPrio;
        this.highPrio = highPrio;
        this.nBuckets = nBuckets;
        this.maxBucketNr = nBuckets - 1;
        this.buckets = new LinkedHashMap[nBuckets];
        for (int i = 0; i < nBuckets; i++) {
            buckets[i] = new LinkedHashMap<K, EntryImpl<K,V>>(256, 0.75F, false);  //TODO: could access-order really be guaranteed to the outside (b/c internal accesses)?
        }
        this.bucketWidth = (highPrio - lowPrio) / nBuckets;
        this.maxTotalCost = maxTotalCost;
        if (elementCostFunction != null) {
            this.elementCostFunction = elementCostFunction;
        } else {
            this.elementCostFunction = new Function1<V, Double>() {
                @Override
                public Double run(V v) {
                    return 1.0;
                }
            };
        }
    }

    protected int prio2bucketNr(double prio) {
        return Math.max(0, Math.min(maxBucketNr,
                (int) ((prio - lowPrio) / bucketWidth)));
    }

    @Override
    public synchronized V put(K k, V v, double priority) {
        V result = null;
        EntryImpl<K,V> newE = new EntryImpl<K,V>(k, v, priority);
        EntryImpl<K,V> oldE = entries.put(k, newE);
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
        EntryImpl<K,V> e = entries.get(k);
        if (e == null) {
            return null;
        } else {
            return e.v;
        }
    }

    @Override
    public synchronized V remove(K k) {
        EntryImpl<K,V> oldE = entries.remove(k);
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
        EntryImpl<K,V> oldE = entries.remove(k);
        if (oldE != null) {
            buckets[prio2bucketNr(oldE.priority)].remove(k);
            EntryImpl<K,V> newE = new EntryImpl<K,V>(k, oldE.v, priority);
            entries.put(k, newE);
            buckets[prio2bucketNr(newE.priority)].put(k, newE);
        }
    }

    @Override
    public synchronized double getCurrentTotalCost() {
        return totalCost;
    }

    @Override
    public synchronized double getMaxTotalCost() {
        return maxTotalCost;
    }

    @Override
    public synchronized void setMaxTotalCost(double maxTotalCost) {
        this.maxTotalCost = maxTotalCost;
        evictExcessElements();
    }

    @Override
    public Function1<V, Double> getElementCostFunction() {
        return elementCostFunction;
    }

    public Iterator<Entry<K,V>> entryIterator()   {
        return new EntryIterator();
    }

    //TODO: synchronization...
    protected class EntryIterator implements Iterator<Entry<K, V>> {
        boolean hasNext;
        Entry<K, V> lastNext;
        int currBucketNo;
        Iterator<Map.Entry<K, EntryImpl<K,V>>> currBucketIterator;

        public EntryIterator() {
            currBucketNo = 0;
            currBucketIterator = buckets[currBucketNo].entrySet().iterator();
            advanceToNext();
        }

        private void advanceToNext() {
            if (currBucketIterator.hasNext()) {
                hasNext = true;
            } else {
                for (int iBucket = currBucketNo + 1; iBucket < nBuckets; iBucket++) {
                    LinkedHashMap<K, EntryImpl<K,V>> bucket = buckets[iBucket];
                    if (bucket.isEmpty()) {
                        continue;
                    }
                    currBucketIterator = bucket.entrySet().iterator();
                    currBucketNo = iBucket;
                    hasNext = true;
                    return;
                }
                hasNext = false;
            }
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastNext = currBucketIterator.next().getValue();
            advanceToNext();
            return lastNext;
        }

        @Override
        public void remove() {
            if (lastNext == null) {
                throw new IllegalStateException();
            }
            BucketedPriorityCache.this.remove(lastNext.getKey());
            lastNext = null;
        }
    };

    protected void evictExcessElements() {
        if (maxTotalCost < 0) {
            return;
        }
        while ((totalCost > maxTotalCost) && (entries.size() > 1)) {
            // ^^^ ensure size >= 1 to always keep at least one element in, even
            // if it alone exceeds maxTotalCost
            for (int iBucket = 0; iBucket < nBuckets; iBucket++) {
                LinkedHashMap<K, EntryImpl<K,V>> bucket = buckets[iBucket];
                if (bucket.isEmpty()) {
                    continue;
                }
                Map.Entry<K, EntryImpl<K,V>> oldestMapEntry = bucket.entrySet()
                        .iterator().next();
                EntryImpl<K,V> oldestEntry = oldestMapEntry.getValue();
                totalCost -= elementCostFunction.run(oldestEntry.v);
                bucket.remove(oldestMapEntry.getKey());
                entries.remove(oldestMapEntry.getKey());
                break;
            }
        }
    }

}

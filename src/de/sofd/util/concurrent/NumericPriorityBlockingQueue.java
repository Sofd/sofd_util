package de.sofd.util.concurrent;

import de.sofd.lang.Function1;
import de.sofd.util.BucketedPriorityCache;
import de.sofd.util.PriorityCache;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * {@link BlockingQueue} implementation that
 * assigns numerical priorities to its elements and orders them in ascending
 * priority order. This is similar to how {@link PriorityBlockingQueue}
 * orders its elements in ascending {@linkplain Comparable natural ordering},
 * except that this implementation provides O(1) (rather than O(log n)) time
 * complexity for all basic operations by using a {@link BucketedPriorityCache}
 * internally.
 * <p>
 * The priority of each element is determined by a <em>priority function</em>,
 * which must be supplied to the constructor of this class.
 * <p>
 * Additionally, you may supply a <em>cost function</em> that determines a
 * "cost" of each element, and a maximum cost up to which the queue can grow.
 * Please note that when the maximum priority is exceeded, elements with low
 * priorities will be evicted as necessary (see {@link PriorityCache} for
 * details). So, what will NOT happen is that methods like <em>put</em> block
 * until other threads have removed enough elements. (something like that may
 * be implemented in the future). For now, only take() will block when the
 * queue is empty.
 * <p>
 * There is a boolean isReverseEviction flag -- if it is true, eviction starts
 * with the highest-priority elements, not the lowest-priority ones.
 *
 * @author olaf
 */
public class NumericPriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {

    private final PriorityCache<E, E> backend;   //key==value in all elements
    private final Function1<E, Double> elementPriorityFunction;
    private final Object lock = new Object();

    public NumericPriorityBlockingQueue(double lowPrio, double highPrio, int nBuckets, Function1<E, Double> elementPriorityFunction) {
        this(lowPrio, highPrio, nBuckets, elementPriorityFunction, -1, null, true);
    }

    public NumericPriorityBlockingQueue(double lowPrio, double highPrio, int nBuckets, Function1<E, Double> elementPriorityFunction, boolean reverseEviction) {
        this(lowPrio, highPrio, nBuckets, elementPriorityFunction, -1, null, reverseEviction);
    }

    public NumericPriorityBlockingQueue(double lowPrio, double highPrio, int nBuckets, Function1<E, Double> elementPriorityFunction, double maxTotalCost, Function1<E, Double> elementCostFunction, boolean reverseEviction) {
        this.backend = new BucketedPriorityCache<E, E>(lowPrio, highPrio, nBuckets, maxTotalCost, elementCostFunction, reverseEviction);
        this.elementPriorityFunction = elementPriorityFunction;
    }


    @Override
    public Iterator<E> iterator() {
        final Iterator<PriorityCache.Entry<E, E>> ei = backend.entryIterator();
        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                synchronized (lock) {
                    return ei.hasNext();
                }
            }

            @Override
            public E next() {
                synchronized (lock) {
                    return ei.next().getValue();
                }
            }

            @Override
            public void remove() {
                synchronized (lock) {
                    ei.remove();
                }
            }
        };
    }

    @Override
    public int size() {
        return backend.size();
    }

    @Override
    public boolean offer(E e) {
        synchronized (lock) {
            backend.put(e, e, elementPriorityFunction.run(e));
            if (backend.size() == 1) {
                lock.notify();
            }
            return true;
        }
    }

    @Override
    public E poll() {
        synchronized (lock) {
            Iterator<PriorityCache.Entry<E, E>> ei = backend.entryIterator();
            if (!ei.hasNext()) {
                return null;
            }
            E e = ei.next().getKey();
            ei.remove();
            return e;
        }
    }

    @Override
    public E peek() {
        synchronized (lock) {
            Iterator<PriorityCache.Entry<E, E>> ei = backend.entryIterator();
            if (!ei.hasNext()) {
                return null;
            }
            return ei.next().getKey();
        }
    }

    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }

    @Override
    public E take() throws InterruptedException {
        synchronized (lock) {
            while (backend.isEmpty()) {
                lock.wait();
            }
            return poll();
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (lock) {
            while (backend.isEmpty()) {
                lock.wait(unit.toMillis(timeout));
            }
            return poll();
        }
    }

    @Override
    public boolean remove(Object o) {
        return null != backend.remove((E)o);
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        for (Iterator<PriorityCache.Entry<E,E>> it = backend.entryIterator(); it.hasNext() && n < maxElements;) {
            c.add(it.next().getValue());
            it.remove();
            ++n;
        }
        return n;
    }

    public double getCurrentTotalCost() {
        return backend.getCurrentTotalCost();
    }

    public void setMaxTotalCost(double value) {
        synchronized (lock) {
            backend.setMaxTotalCost(value);
        }
    }

    public double getMaxTotalCost() {
        return backend.getMaxTotalCost();
    }

    public boolean isReverseEviction() {
        return backend.isReverseEviction();
    }

    public void setReverseEviction(boolean reverseEviction) {
        backend.setReverseEviction(reverseEviction);
    }
}

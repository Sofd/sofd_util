package de.sofd.util.concurrent;

import de.sofd.lang.Function1;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author olaf
 */
public class NumericPriorityThreadPoolExecutor extends ThreadPoolExecutor {

    public NumericPriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, double lowPrio, double highPrio, int nBuckets, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new NumericPriorityBlockingQueue<Runnable>(lowPrio, highPrio, nBuckets, elemPrioFunction, true), threadFactory, handler);
    }

    public NumericPriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, double lowPrio, double highPrio, int nBuckets, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new NumericPriorityBlockingQueue<Runnable>(lowPrio, highPrio, nBuckets, elemPrioFunction, true), handler);
    }

    public NumericPriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, double lowPrio, double highPrio, int nBuckets, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new NumericPriorityBlockingQueue<Runnable>(lowPrio, highPrio, nBuckets, elemPrioFunction, true), threadFactory);
    }

    public NumericPriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, double lowPrio, double highPrio, int nBuckets) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new NumericPriorityBlockingQueue<Runnable>(lowPrio, highPrio, nBuckets, elemPrioFunction, true));
    }

    private static Function1<Runnable, Double> elemPrioFunction = new Function1<Runnable, Double>() {
        @Override
        public Double run(Runnable r) {
            return ((PrioritizedTask<?>)r).getPriority();
        }
    };


    //covariant return types

    @Override
    public PrioritizedTask<?> submit(Runnable task) {
        return (PrioritizedTask<?>) super.submit(task);
    }

    @Override
    public <T> PrioritizedTask<T> submit(Runnable task, T result) {
        return (PrioritizedTask<T>) super.submit(task, result);
    }

    @Override
    public <T> PrioritizedTask<T> submit(Callable<T> task) {
        return (PrioritizedTask<T>) super.submit(task);
    }

    //TODO: continue...
}

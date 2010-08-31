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
    public PrioritizedTask<Object> submit(Runnable task) {
        return submitWithPriority(task, 0);
    }

    @Override
    public <T> PrioritizedTask<T> submit(Runnable task, T result) {
        return submitWithPriority(task, result, 0);
    }

    @Override
    public <T> PrioritizedTask<T> submit(Callable<T> task) {
        return submitWithPriority(task, 0);
    }

    public PrioritizedTask<Object> submitWithPriority(Runnable task, double priority) {
        return submitWithPriority(task, null, priority);
    }

    public <T> PrioritizedTask<T> submitWithPriority(Runnable task, T result, double priority) {
        if (task == null) throw new NullPointerException();
        PrioritizedTask<T> ftask = newTaskFor(task, result);
        ftask.priority = priority;
        execute(ftask);
        return ftask;
    }

    public <T> PrioritizedTask<T> submitWithPriority(Callable<T> callable, double priority) {
        if (callable == null) throw new NullPointerException();
        PrioritizedTask<T> ftask = newTaskFor(callable);
        ftask.priority = priority;
        execute(ftask);
        return ftask;
    }

    public <T> PrioritizedTask<T> resubmitWithPriority(PrioritizedTask<T> task, double priority) {
        if (!remove(task)) {
            throw new IllegalArgumentException();
        }
        PrioritizedTask<T> ftask;
        if (null != task.wrappedRunnable) {
            ftask = newTaskFor(task.wrappedRunnable, task.wrappedResult);
        } else {
            ftask = newTaskFor(task.wrappedCallable);
        }
        ftask.priority = priority;
        execute(ftask);
        return ftask;
    }



    @Override
    protected <T> PrioritizedTask<T> newTaskFor(Runnable runnable, T value) {
        PrioritizedTask<T> result = new PrioritizedTask<T>(runnable, value);
        if (runnable instanceof PrioritizedTask) {
             result.priority = ((PrioritizedTask)runnable).getPriority();
        }
        result.owner = this;
        return result;
    }

    @Override
    protected <T> PrioritizedTask<T> newTaskFor(Callable<T> callable) {
        PrioritizedTask result = new PrioritizedTask<T>(callable);
        if (callable instanceof PrioritizedTask) {
             result.priority = ((PrioritizedTask)callable).getPriority();
        }
        result.owner = this;
        return result;
    }

}

package de.sofd.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * FutureTask objects handed out by {@link NumericPriorityThreadPoolExecutor}.
 * In addition to wrapping a Runnable/Callable task (like the superclass does),
 * this one also holds the priority of the task.
 * <p>
 * Note that the priority is immutable in this task object; for changing the
 * priority of a task you have to use
 * {@link NumericPriorityThreadPoolExecutor#resubmitWithPriority(de.sofd.util.concurrent.PrioritizedTask, double) }.
 *
 * @author olaf
 */
public class PrioritizedTask<T> extends FutureTask<T> {
    double priority = 0.0;
    Runnable wrappedRunnable;
    T wrappedResult;
    Callable<T> wrappedCallable;

    NumericPriorityThreadPoolExecutor owner;

    PrioritizedTask(Runnable r, T result) {  //package-private
        super(r, result);
        wrappedRunnable = r;
        wrappedResult = result;
    }

    PrioritizedTask(Callable<T> c) {    //package-private
        super(c);
        wrappedCallable = c;
    }

    public double getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "PT: [" + (wrappedRunnable != null ? wrappedRunnable : wrappedCallable) + "] prio=" + getPriority();
    }

}

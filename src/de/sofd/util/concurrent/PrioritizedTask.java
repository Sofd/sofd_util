package de.sofd.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
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

}

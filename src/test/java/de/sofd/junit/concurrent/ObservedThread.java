package de.sofd.junit.concurrent;

import java.util.Collection;

/**
 *
 * @author olaf
 */
public abstract class ObservedThread extends Thread {

    private final Collection<Throwable> threadErrors;
    private final boolean outputStackTrace;

    public ObservedThread(Collection<Throwable> threadErrors) {
        this(threadErrors, true);
    }

    public ObservedThread(Collection<Throwable> threadErrors, boolean outputStackTrace) {
        this.threadErrors = threadErrors;
        this.outputStackTrace = outputStackTrace;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Throwable t) {
            if (outputStackTrace) {
                t.printStackTrace();
            }
            threadErrors.add(t);
        }
    }

    protected abstract void doRun() throws Exception;

}

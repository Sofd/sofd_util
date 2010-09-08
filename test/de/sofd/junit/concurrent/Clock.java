package de.sofd.junit.concurrent;

import static org.junit.Assert.*;

/**
 * Clock class that can measure the time since its creation and make
 * JUnit assertions about it. The accuracy of the measurement may
 * be deliberately lowered to make the whole thing less sensitive
 * to varying system loads etc. The accuracy is set by specifying
 * the "tick" of the clock, which is a number of milliseconds. All
 * time measurements will be taken as (and rounded to)
 * numbers of ticks then.
 *
 * <p>
 *
 * The default tick is 200 ms.
 *
 * <p>
 *
 * This class is mainly meant to be
 * used in JUnit tests that want to measure running times (e.g. to
 * test that the performance of an algorithm hasn't dropped) or to
 * measure the relative time of certain events.
 *
 * <p>
 *
 * All public methods of this class are ALSO available as static
 * versions, which will operate on an automatically created default
 * clock created via the default constructor.
 *
 * <p>
 *
 * The class is thread-safe.
 *
 * @author olaf
 */
public class Clock {

    private long startTimeInMillis;
    private final long tick;

    public Clock() {
        this(System.currentTimeMillis(), 200);
    }

    public Clock(int tick) {
        this(System.currentTimeMillis(), tick);
    }

    public Clock(long startTimeInMillis, int tick) {
        this.startTimeInMillis = startTimeInMillis;
        this.tick = tick;
    }

    public long getTick() {
        return tick;
    }

    public void startOrRestart() {
        this.startTimeInMillis = System.currentTimeMillis();
    }

    public void sleep(double ticks) {
        try {
            Thread.sleep((long)(tick*ticks));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public long getCurrentTime() {
        return (System.currentTimeMillis() - startTimeInMillis + tick/2)/tick;
    }

    public void assertCurrentTimeIs(long ticks) {
        assertEquals(getCurrentTime(), ticks);
    }


    private static Clock defaultClock = new Clock(500);

    public static long getClockTick() {
        return defaultClock.getTick();
    }

    public static void startOrRestartClock() {
        defaultClock.startOrRestart();
    }

    public static void clockSleep(double ticks) {
        defaultClock.sleep(ticks);
    }

    public static long getCurrentClockTime() {
        return defaultClock.getCurrentTime();
    }

    public static void assertCurrentClockTimeIs(long ticks) {
        defaultClock.assertCurrentTimeIs(ticks);
    }

    public static Clock getDefaultClock() {
        return defaultClock;
    }

}

package de.sofd.util.concurrent;

import de.sofd.junit.concurrent.Message;
import static de.sofd.junit.concurrent.Clock.*;
import static de.sofd.junit.concurrent.MessageLog.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author olaf
 */
public class NumericPriorityThreadPoolExecutorTest {

    private static class Sleeper implements Runnable {
        private final String name;
        private final long sleepTime;

        public Sleeper(String name, int sleepTime) {
            this.name = name;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            writeLogMessage(name+" started");
            clockSleep(sleepTime);
            writeLogMessage(name+" finished");
        }

        @Override
        public String toString() {
            return "Sleeper: " + name + " (" + sleepTime + " ticks)";
        }

    }

    public NumericPriorityThreadPoolExecutorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOneThread() throws Exception {
        System.out.println("testOneThread");
        clearMessageLog();
        startOrRestartClock();
        NumericPriorityThreadPoolExecutor e1 = NumericPriorityThreadPoolExecutor.newFixedThreadPool(1, 10);
        e1.submit(new Sleeper("sleeper7ticks",7));
        e1.submit(new Sleeper("sleeper12ticks",12));
        clockSleep(3);
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
        });
        clockSleep(7);
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
                new Message(7,"sleeper7ticks finished"),
                new Message(7,"sleeper12ticks started"),
        });
        clockSleep(20);
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
                new Message(7,"sleeper7ticks finished"),
                new Message(7,"sleeper12ticks started"),
                new Message(19,"sleeper12ticks finished"),
        });
    }

    @Test
    public void testTwoThreads() throws Exception {
        System.out.println("testTwoThreads");
        clearMessageLog();
        startOrRestartClock();
        NumericPriorityThreadPoolExecutor e = NumericPriorityThreadPoolExecutor.newFixedThreadPool(2, 10);
        e.submit(new Sleeper("sleeper7ticks",7));
        e.submit(new Sleeper("sleeper12ticks",12));
        clockSleep(3);
        assertLogMessagesEqual(
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started")
        );
        clockSleep(7);
        assertLogMessagesEqual(
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started"),
                new Message(7,"sleeper7ticks finished")
        );
        clockSleep(13);
        assertLogMessagesEqual(
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started"),
                new Message(7,"sleeper7ticks finished"),
                new Message(12,"sleeper12ticks finished")
        );
    }

    @Test
    public void testOneThreadWithPriorities() throws Exception {
        System.out.println("testOneThreadWithPriorities");
        clearMessageLog();
        startOrRestartClock();
        NumericPriorityThreadPoolExecutor e = NumericPriorityThreadPoolExecutor.newFixedThreadPool(1, 10);
        e.submitWithPriority(new Sleeper("normprio1",3), 5);
        clockSleep(1);
        e.submitWithPriority(new Sleeper("normprio2",3), 5);
        e.submitWithPriority(new Sleeper("normprio3",3), 5);
        clockSleep(1);
        e.submitWithPriority(new Sleeper("highprio1",3), 0);
        e.submitWithPriority(new Sleeper("lowprio1",3), 10);
        e.submitWithPriority(new Sleeper("highprio2",3), 0);
        clockSleep(0.5);
        e.submitWithPriority(new Sleeper("highprio3",3), 0);
        assertLogMessagesEqual(
                new Message(0,"normprio1 started")
        );
        clockSleep(2);
        assertLogMessagesEqual(
                new Message(0,"normprio1 started"),
                new Message(3,"normprio1 finished"),
                new Message(3,"highprio1 started")
        );
        clockSleep(30);
        assertLogMessagesEqual(
                new Message(0,"normprio1 started"),
                new Message(3,"normprio1 finished"),
                new Message(3,"highprio1 started"),
                new Message(6,"highprio1 finished"),
                new Message(6,"highprio2 started"),
                new Message(9,"highprio2 finished"),
                new Message(9,"highprio3 started"),
                new Message(12,"highprio3 finished"),
                new Message(12,"normprio2 started"),
                new Message(15,"normprio2 finished"),
                new Message(15,"normprio3 started"),
                new Message(18,"normprio3 finished"),
                new Message(18,"lowprio1 started"),
                new Message(21,"lowprio1 finished")
        );
    }

    @Test
    public void testTwoThreadsWithPriorities() throws Exception {
        System.out.println("testTwoThreadsWithPriorities");
        clearMessageLog();
        startOrRestartClock();
        NumericPriorityThreadPoolExecutor e = NumericPriorityThreadPoolExecutor.newFixedThreadPool(2, 10);
        e.submitWithPriority(new Sleeper("normprio1",4), 5);
        clockSleep(1);
        e.submitWithPriority(new Sleeper("normprio2",4), 5);
        e.submitWithPriority(new Sleeper("normprio3",4), 5);
        clockSleep(1);
        e.submitWithPriority(new Sleeper("highprio1",4), 0);
        e.submitWithPriority(new Sleeper("lowprio1",4), 10);
        e.submitWithPriority(new Sleeper("highprio2",4), 0);
        clockSleep(1);
        e.submitWithPriority(new Sleeper("highprio3",4), 0);
        clockSleep(20);
        assertLogMessagesEqual(
                new Message(0,"normprio1 started"),
                new Message(1,"normprio2 started"),
                new Message(4,"normprio1 finished"),
                new Message(4,"highprio1 started"),
                new Message(5,"normprio2 finished"),
                new Message(5,"highprio2 started"),
                new Message(8,"highprio1 finished"),
                new Message(8,"highprio3 started"),
                new Message(9,"highprio2 finished"),
                new Message(9,"normprio3 started"),
                new Message(12,"highprio3 finished"),
                new Message(12,"lowprio1 started"),
                new Message(13,"normprio3 finished"),
                new Message(16,"lowprio1 finished")
        );
    }

}
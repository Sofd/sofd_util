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
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started"),
        });
        clockSleep(7);
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started"),
                new Message(7,"sleeper7ticks finished"),
        });
        clockSleep(13);
        assertLogMessagesEqual(new Message[]{
                new Message(0,"sleeper7ticks started"),
                new Message(0,"sleeper12ticks started"),
                new Message(7,"sleeper7ticks finished"),
                new Message(12,"sleeper12ticks finished"),
        });
    }

}
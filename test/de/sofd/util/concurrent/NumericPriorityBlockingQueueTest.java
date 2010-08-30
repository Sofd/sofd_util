package de.sofd.util.concurrent;

import de.sofd.junit.concurrent.ObservedThread;
import de.sofd.lang.Function1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import static de.sofd.junit.concurrent.Clock.*;
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
public class NumericPriorityBlockingQueueTest {

    protected static class EltValue {
        private String id;
        private double priority;
        private double cost;

        public EltValue(String id, double priority) {
            this(id, priority, 1);
        }

        public EltValue(String id, double priority, double cost) {
            this.id = id;
            this.priority = priority;
            this.cost = cost;
        }

        public String getId() {
            return id;
        }

        public double getPriority() {
            return priority;
        }

        public double getCost() {
            return cost;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(cost);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            temp = Double.doubleToLongBits(priority);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EltValue other = (EltValue) obj;
            if (Double.doubleToLongBits(cost) != Double
                    .doubleToLongBits(other.cost))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (Double.doubleToLongBits(priority) != Double
                    .doubleToLongBits(other.priority))
                return false;
            return true;
        }

    }

    private static Function1<EltValue, Double> prioFunction = new Function1<EltValue, Double>() {
        @Override
        public Double run(EltValue p0) {
            return p0.getPriority();
        }
    };

    private static Function1<EltValue, Double> costFunction = new Function1<EltValue, Double>() {
        @Override
        public Double run(EltValue p0) {
            return p0.getCost();
        }
    };

    public NumericPriorityBlockingQueueTest() {
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
    public void testBasicEnqueueDequeueST() throws Exception {
        System.out.println("testBasicEnqueueDequeueST");
        NumericPriorityBlockingQueue<EltValue> q = new NumericPriorityBlockingQueue<EltValue>(0, 100, 10, prioFunction);
        q.put(new EltValue("foo", 10));
        q.offer(new EltValue("bar", 10));
        q.add(new EltValue("baz", 10));
        assertEquals(3, q.size());
        assertEquals("foo", q.peek().getId());
        assertEquals("foo", q.peek().getId());
        assertEquals("foo", q.peek().getId());
        assertEquals("foo", q.take().getId());
        assertEquals("bar", q.take().getId());
        q.put(new EltValue("hello", 10));
        assertEquals("baz", q.poll().getId());
        q.put(new EltValue("quux", 10));
        q.put(new EltValue("world", 10));
        assertEquals("hello", q.take().getId());
        assertEquals("quux", q.take().getId());
        assertEquals("world", q.take().getId());
        assertNull(q.poll());
        assertNull(q.poll());
        assertTrue(q.isEmpty());
    }
    
    @Test
    public void testEnqueueAndRemoveST() throws Exception {
        System.out.println("EnqueueAndRemoveST");
        NumericPriorityBlockingQueue<EltValue> q = new NumericPriorityBlockingQueue<EltValue>(0, 100, 10, prioFunction);
        q.put(new EltValue("foo", 10));
        q.offer(new EltValue("bar", 10));
        q.add(new EltValue("baz", 10));
        q.add(new EltValue("quux", 10));
        q.add(new EltValue("blah", 10));
        q.add(new EltValue("blubb", 10));
        q.add(new EltValue("hello", 10));
        assertEquals(7, q.size());
        assertEquals("foo", q.take().getId());
        assertEquals("bar", q.poll().getId());
        assertEquals("baz", q.remove().getId());
        assertFalse(q.remove(new EltValue("blubb", 20)));
        assertFalse(q.remove(new EltValue("blubbbbb", 10)));
        assertTrue(q.remove(new EltValue("blubb", 10)));
        assertFalse(q.remove(new EltValue("blubb", 10)));
        assertEquals("quux", q.poll().getId());
        assertEquals("blah", q.poll().getId());
        assertEquals("hello", q.poll().getId());
        assertNull(q.poll());
        assertNull(q.poll());
        assertTrue(q.isEmpty());
    }

    @Test
    public void testBasicEnqueueDequeueMT() throws Exception {
        System.out.println("BasicEnqueueDequeueMT");

        final NumericPriorityBlockingQueue<EltValue> q = new NumericPriorityBlockingQueue<EltValue>(0, 100, 10, prioFunction);

        startOrRestartClock();

        System.out.println("t0: putting foo,bar,baz");
        q.put(new EltValue("foo", 10));
        q.put(new EltValue("bar", 10));
        q.put(new EltValue("baz", 10));

        final Collection<Throwable> threadErrors = Collections.synchronizedCollection(new ArrayList<Throwable>());
        Thread t1 = new ObservedThread(threadErrors) {
            @Override
            protected void doRun() throws Exception {
                System.out.println("t1: taking foo,bar,baz");
                assertEquals("foo", q.take().getId());
                assertEquals("bar", q.take().getId());
                assertEquals("baz", q.take().getId());
                System.out.println("t1: sleep 4");
                clockSleep(4);
                System.out.println("t1: putting hello world");
                q.put(new EltValue("hello", 10));
                q.put(new EltValue("world", 10));
            }
        };
        t1.start();

        assertCurrentClockTimeIs(0);
        System.out.println("t0: sleep 2");
        clockSleep(2);  // avoid race by giving t1 time to remove the two elements
        assertCurrentClockTimeIs(2);
        System.out.println("t0: taking hello");
        assertNull(q.poll());
        try {
            q.remove();
            fail("NoSuchElementException expected");
        } catch (NoSuchElementException e) {
        }
        assertCurrentClockTimeIs(2);
        assertEquals("hello", q.take().getId());  //take() should block for 2 ticks until t1 puts the hello element in
        assertCurrentClockTimeIs(4);
        System.out.println("t0: taking world");
        assertEquals("world", q.take().getId());
        assertCurrentClockTimeIs(4);

        t1.join();
        if (!threadErrors.isEmpty()) {
            fail("there were errors in threads -- see output");
        }
    }


    @Test
    public void testPrioritizedEnqueueDequeueMT() throws Exception {
        System.out.println("PrioritizedEnqueueDequeueMT");

        final NumericPriorityBlockingQueue<EltValue> q = new NumericPriorityBlockingQueue<EltValue>(0, 100, 15, prioFunction, 500, costFunction, false);

        startOrRestartClock();

        final Collection<Throwable> threadErrors = Collections.synchronizedCollection(new ArrayList<Throwable>());
        Thread t1 = new ObservedThread(threadErrors) {
            @Override
            protected void doRun() throws Exception {
                assertCurrentClockTimeIs(0);
                System.out.println("t1: taking 1st");
                assertEquals("p40-c70", q.take().getId());
                assertCurrentClockTimeIs(3);
                clockSleep(4);
                System.out.println("t1: taking others");
                assertEquals("p30-c100", q.take().getId());
                assertEquals("p40-c100", q.take().getId());
                assertEquals("p50-c50", q.take().getId());
                assertEquals("p60-c70", q.take().getId());
                assertEquals("p80-c120", q.take().getId());
            }
        };
        t1.start();

        clockSleep(3);
        System.out.println("t0: putting");
        q.put(new EltValue("p40-c70", 40, 70));

        clockSleep(2);
        System.out.println("t0: putting others");
        q.put(new EltValue("p60-c70", 60, 70));
        q.put(new EltValue("p40-c100", 40, 100));
        q.put(new EltValue("p50-c50", 50, 50));
        q.put(new EltValue("p80-c120", 80, 120));
        q.put(new EltValue("p20-c90", 20, 90));
        assertEquals(430, q.getCurrentTotalCost(), 0.001);
        q.put(new EltValue("p30-c100", 30, 100));
        assertEquals(440, q.getCurrentTotalCost(), 0.001);

        t1.join();
        if (!threadErrors.isEmpty()) {
            fail("there were errors in threads -- see output");
        }
    }
}
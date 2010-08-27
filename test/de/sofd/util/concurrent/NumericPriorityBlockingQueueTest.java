package de.sofd.util.concurrent;

import de.sofd.lang.Function1;
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
        System.out.println("BasicST");
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
        System.out.println("BasicST");
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

}
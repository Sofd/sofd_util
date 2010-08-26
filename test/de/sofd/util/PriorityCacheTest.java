package de.sofd.util;

import de.sofd.lang.Function1;
import java.util.Iterator;
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
public class PriorityCacheTest {

    protected static class EltValue {
        private String id;
        private double cost;

        public EltValue(String id, double cost) {
            this.id = id;
            this.cost = cost;
        }

        public String getId() {
            return id;
        }

        public double getCost() {
            return cost;
        }
    }

    private static Function1<EltValue, Double> costFunction = new Function1<EltValue, Double>() {

        @Override
        public Double run(EltValue p0) {
            return p0.getCost();
        }

    };

    public PriorityCacheTest() {
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
    public void testSimpleMapping() {
        System.out.println("SimpleMapping");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 10, 1000, costFunction);
        assertTrue(pc.isEmpty());
        pc.put("foo", new EltValue("foo", 1), 0);
        pc.put("bar", new EltValue("bar", 1), 0);
        pc.put("baz", new EltValue("baz", 1), 0);
        assertFalse(pc.isEmpty());
        assertEquals("3 elts expected", 3, pc.size());
        assertEquals("foo", pc.get("foo").getId());
        assertEquals("bar", pc.get("bar").getId());
        assertEquals("baz", pc.get("baz").getId());

        assertTrue(pc.contains("foo"));
        assertTrue(pc.contains("bar"));
        assertTrue(pc.contains("baz"));
        assertFalse(pc.contains("quux"));

        assertEquals("bar", pc.remove("bar").getId());
        assertEquals(2, pc.size());
        assertFalse(pc.contains("bar"));
    }

    @Test
    public void testSimpleIteration() {
        System.out.println("SimpleIteration");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 10, 1000, costFunction);
        assertTrue(pc.isEmpty());
        pc.put("foo", new EltValue("foo", 1), 0);
        pc.put("bar", new EltValue("bar", 1), 0);
        pc.put("baz", new EltValue("baz", 1), 0);
        assertFalse(pc.isEmpty());
        assertIterationValues(pc, "foo", "bar", "baz");
    }

    protected void assertIterationValues(PriorityCache<String, EltValue> pc, String... values) {
        Iterator<PriorityCache.Entry<String, EltValue>> it = pc.entryIterator();
        for (String v : values) {
            assertTrue(it.hasNext());
            PriorityCache.Entry<String, EltValue> e = it.next();
            assertEquals(v, e.getValue().getId());
        }
        assertFalse(it.hasNext());
    }

    @Test
    public void testTotalCost() {
        System.out.println("TotalCost");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 10, 1000, costFunction);
        assertTrue(pc.isEmpty());
        assertEquals(0, pc.getCurrentTotalCost(), 0.001);
        pc.put("foo", new EltValue("foo", 10), 0);
        assertEquals(10, pc.getCurrentTotalCost(), 0.001);
        pc.put("bar", new EltValue("bar", 20), 0);
        assertEquals(30, pc.getCurrentTotalCost(), 0.001);
        pc.put("baz", new EltValue("baz", 100), 0);
        assertEquals(130, pc.getCurrentTotalCost(), 0.001);

        assertEquals("bar", pc.remove("bar").getId());
        assertEquals(110, pc.getCurrentTotalCost(), 0.001);
    }

    @Test
    public void testPriorities() {
        System.out.println("Priorities");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 10, 500, costFunction);
        assertTrue(pc.isEmpty());
        assertEquals(0, pc.getCurrentTotalCost(), 0.001);
        pc.put("c100-p50", new EltValue("1", 100), 50);
        pc.put("c130-p40", new EltValue("2", 130), 40);
        assertEquals(230, pc.getCurrentTotalCost(), 0.001);
        pc.put("c80-p100", new EltValue("3", 80), 100);
        assertEquals(310, pc.getCurrentTotalCost(), 0.001);
        pc.put("c110-p60", new EltValue("4", 110), 60);
        assertEquals(420, pc.getCurrentTotalCost(), 0.001);
        assertEquals(4, pc.size());
        assertIterationValues(pc, "2", "1", "4", "3");
        pc.put("c150-p60", new EltValue("5", 150), 60); // cost 500 exceeded => lowest-prio element (c130-p40) evicted
        assertEquals(4, pc.size());
        assertEquals(440, pc.getCurrentTotalCost(), 0.001); // 420 + 150 - 130
        assertNull(pc.get("c130-p40"));

        pc.put("c50-p40", new EltValue("6", 50), 40);
        assertEquals(5, pc.size());
        assertEquals(490, pc.getCurrentTotalCost(), 0.001);
        assertIterationValues(pc, "6", "1", "4", "5", "3");

        pc.put("c200-p70", new EltValue("7", 200), 70); // should evict c50-p40, c100-p50, c110-p60 (NOT c150-p60 b/c of insertion-ordered eviction for equal-prio elts.)
        assertEquals(3, pc.size());
        assertEquals(430, pc.getCurrentTotalCost(), 0.001); // 490 + 200 - 50 - 100 - 110
        // check that the expected ones are still in there
        assertEquals("3", pc.get("c80-p100").getId());
        assertEquals("5", pc.get("c150-p60").getId());
        assertEquals("7", pc.get("c200-p70").getId());
        assertIterationValues(pc, "5", "7", "3");

        pc.setMaxTotalCost(200);   // should evict c150-p60, c200-p70
        assertEquals(1, pc.size());
        assertEquals(80, pc.getCurrentTotalCost(), 0.001);
        assertEquals("3", pc.get("c80-p100").getId());

        pc.put("c30-p40", new EltValue("8", 30), 40);
        pc.put("c20-p50", new EltValue("9", 20), 50);
        pc.put("c40-p40", new EltValue("10", 40), 40);
        assertEquals(170, pc.getCurrentTotalCost(), 0.001);
        pc.setPriority("c80-p100", 20); // lower c80-p100's priority from 100 to 20
        pc.put("c60-p50", new EltValue("11", 60), 50);  // should evict it
        assertEquals(150, pc.getCurrentTotalCost(), 0.001); // 170+60-80
        assertEquals(4, pc.size());
        assertNull(pc.get("c80-p100"));
        assertIterationValues(pc, "8", "10", "9", "11");
        //assertion for access-ordered iteration -- not implemented atm.
        //assertEquals(40, pc.get("c40-p40").getCost(), 0.001);
        //assertIterationValues(pc, "10", "8", "9", "11");

        pc.setMaxTotalCost(-1);  //disable cost upper limit
        pc.put("c200-p85", new EltValue("12", 200), 85);
        pc.put("c400-p45", new EltValue("13", 400), 45);
        assertEquals(750, pc.getCurrentTotalCost(), 0.001);
        assertIterationValues(pc, "8", "10", "13", "9", "11", "12");
    }

}
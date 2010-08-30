package de.sofd.util;

import de.sofd.lang.Function1;
import static de.sofd.util.MoreCollections.*;
import java.util.ArrayList;
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
        pc.put("1", new EltValue("foo", 10), 30);
        pc.put("2", new EltValue("bar", 50), 90);
        pc.put("3", new EltValue("baz", 20), 70);
        assertFalse(pc.isEmpty());
        assertIterationValues(pc, false, "foo", "baz", "bar");
        assertIterationValues(pc, true, "bar", "baz", "foo");

        Iterator<PriorityCache.Entry<String, EltValue>>  it = pc.entryIterator();
        assertTrue(it.hasNext());
        PriorityCache.Entry<String, EltValue> ce = it.next();
        assertEquals("1", ce.getKey());
        assertEquals("foo", ce.getValue().getId());
        assertEquals(10, ce.getValue().getCost(), 0.001);
        assertTrue(it.hasNext());
        ce = it.next();
        assertEquals("3", ce.getKey());
        assertEquals("baz", ce.getValue().getId());
        assertEquals(20, ce.getValue().getCost(), 0.001);
        it.remove();
        assertTrue(it.hasNext());
        ce = it.next();
        assertEquals("2", ce.getKey());
        assertEquals("bar", ce.getValue().getId());
        assertEquals(50, ce.getValue().getCost(), 0.001);
        assertFalse(it.hasNext());
        assertIterationValues(pc, false, "foo", "bar");
        assertIterationValues(pc, true,  "bar", "foo");
        assertEquals(2, pc.size());

        ArrayList<EltValue> drainage = new ArrayList<EltValue>();
        for (it = pc.reverseEntryIterator(); it.hasNext();) {
            drainage.add(it.next().getValue());
            it.remove();
        }
        assertTrue(pc.isEmpty());
        assertEquals(2, drainage.size());
        assertEquals("bar", drainage.get(0).getId());
        assertEquals("foo", drainage.get(1).getId());
    }

    protected void assertIterationValues(PriorityCache<String, EltValue> pc, boolean reverse, String... values) {
        Iterator<PriorityCache.Entry<String, EltValue>> it = reverse ? pc.reverseEntryIterator() : pc.entryIterator();
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
        assertIterationValues(pc, false, "2", "1", "4", "3");
        pc.put("c150-p60", new EltValue("5", 150), 60); // cost 500 exceeded => lowest-prio element (c130-p40) evicted
        assertEquals(4, pc.size());
        assertEquals(440, pc.getCurrentTotalCost(), 0.001); // 420 + 150 - 130
        assertNull(pc.get("c130-p40"));

        pc.put("c50-p40", new EltValue("6", 50), 40);
        assertEquals(5, pc.size());
        assertEquals(490, pc.getCurrentTotalCost(), 0.001);
        assertIterationValues(pc, false, "6", "1", "4", "5", "3");
        assertIterationValues(pc, true, "3", "4", "5", "1", "6");

        pc.put("c200-p70", new EltValue("7", 200), 70); // should evict c50-p40, c100-p50, c110-p60 (NOT c150-p60 b/c of insertion-ordered eviction for equal-prio elts.)
        assertEquals(3, pc.size());
        assertEquals(430, pc.getCurrentTotalCost(), 0.001); // 490 + 200 - 50 - 100 - 110
        // check that the expected ones are still in there
        assertEquals("3", pc.get("c80-p100").getId());
        assertEquals("5", pc.get("c150-p60").getId());
        assertEquals("7", pc.get("c200-p70").getId());
        assertIterationValues(pc, false, "5", "7", "3");

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
        assertIterationValues(pc, false, "8", "10", "9", "11");
        //assertion for access-ordered iteration -- not implemented atm.
        //assertEquals(40, pc.get("c40-p40").getCost(), 0.001);
        //assertIterationValues(pc, "10", "8", "9", "11");

        pc.setMaxTotalCost(-1);  //disable cost upper limit
        pc.put("c200-p85", new EltValue("12", 200), 85);
        pc.put("c400-p45", new EltValue("13", 400), 45);
        assertEquals(750, pc.getCurrentTotalCost(), 0.001);
        assertIterationValues(pc, false, "8", "10", "13", "9", "11", "12");

        ArrayList<EltValue> drainage = new ArrayList<EltValue>();
        for (Iterator<PriorityCache.Entry<String, EltValue>> it = pc.entryIterator(); it.hasNext();) {
            drainage.add(it.next().getValue());
            it.remove();
        }
        assertTrue(pc.isEmpty());
        assertArrayEquals(new Object[]{"8", "10", "13", "9", "11", "12"}, mappedCollection(drainage, new Function1<EltValue, String>() {
            @Override
            public String run(EltValue v) {
                return v.getId();
            }
        }).toArray());
    }

    @Test
    public void testPriorities2() {
        System.out.println("Priorities2");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 30, 500, costFunction);
        assertTrue(pc.isEmpty());
        assertEquals(0, pc.getCurrentTotalCost(), 0.001);
        pc.put("c30-p50", new EltValue("1", 30), 50);
        pc.put("c40-p40", new EltValue("2", 40), 40);
        pc.put("c20-p20", new EltValue("3", 20), 20);
        pc.put("c50-p25", new EltValue("4", 50), 25);
        pc.put("c30-p35", new EltValue("5", 30), 35);
        pc.put("c40-p25", new EltValue("6", 40), 25);
        pc.put("c20-p25", new EltValue("7", 20), 25);
        pc.put("c25-p24", new EltValue("8", 25), 24);
        assertEquals(255, pc.getCurrentTotalCost(), 0.001);
        pc.put("c380-p60", new EltValue("9", 380), 60);
        assertEquals(480, pc.getCurrentTotalCost(), 0.001);
        assertEquals(4, pc.size());
    }

    @Test
    public void testDefaultUnlimitedCost() {
        System.out.println("DefaultUnlimitedCost");
        PriorityCache<String, EltValue> pc = new BucketedPriorityCache<String, EltValue>(0, 100, 10, -1, null);
        assertTrue(pc.isEmpty());
        assertEquals(0, pc.getCurrentTotalCost(), 0.001);
        pc.put("foo", new EltValue("foo", 3000), 0);
        assertEquals(1, pc.getCurrentTotalCost(), 0.001);
        pc.put("bar", new EltValue("bar", 2000), 0);
        assertEquals(2, pc.getCurrentTotalCost(), 0.001);
        pc.put("baz", new EltValue("baz", 5000), 0);
        assertEquals(3, pc.getCurrentTotalCost(), 0.001);

        assertEquals("bar", pc.remove("bar").getId());
        assertEquals(2, pc.getCurrentTotalCost(), 0.001);
    }

}
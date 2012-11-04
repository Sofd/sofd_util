/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.sofd.util;

import de.sofd.lang.Function1;
import static de.sofd.util.MoreCollections.*;
import java.util.ArrayList;
import java.util.Collection;
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
public class MoreCollectionsTest {

    public MoreCollectionsTest() {
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

    /**
     * Test of filteredCollection method, of class MoreCollections.
     */
    @Test
    public void testFilteredCollection() {
        System.out.println("filteredCollection");
        Collection<Integer> c = new ArrayList<Integer>();
        c.add(56);
        c.add(23);
        c.add(59);
        c.add(72);
        c.add(12);
        c.add(48);
        c.add(35);
        c.add(46);
        c.add(24);
        c.add(97);
        c.add(35);

        Collection<Integer> filtered = filteredCollection(c, new Predicate<Integer>() {
            @Override
            public boolean holdsFor(Integer x) {
                return (x > 40);
            }
        });

        assertArrayEquals(new Object[]{56,59,72,48,46,97}, filtered.toArray());
    }

    /**
     * Test of mappedCollection method, of class MoreCollections.
     */
    @Test
    public void testMappedCollection() {
        System.out.println("mappedCollection");
        Collection<Integer> c = new ArrayList<Integer>();
        c.add(56);
        c.add(23);
        c.add(24);
        c.add(35);

        Collection<String> mapped = mappedCollection(c, new Function1<Integer, String>() {

            @Override
            public String run(Integer p0) {
                return "foo" + p0;
            }
        });
        assertArrayEquals(new Object[]{"foo56","foo23","foo24","foo35"}, mapped.toArray());
    }

}
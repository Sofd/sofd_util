package de.sofd.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for passing data down the callstack without having to introduce
 * additional method parameters to all the methods along the way and without
 * having to resort to some kind of global/shared variable.
 * <p>
 * In Lisp, one would use what's called "dynamic scoping" for this, hence the
 * name of this class.
 * <p>
 * Usage: To pass on your data, you do something like
 * 
 * <code>
 * DynScope.runWith("fire_events", new Runnable() {
 *    
 *    public void run() {
 *        // ... arbitrary code here .. call other methods etc.
 *    }
 * }
 * </code>
 * 
 * Here, "fire_events" is the piece of data you're passing down the callstack
 * beginning at the run() implementation. Inside run() and in any piece of code
 * that run() calls (directly or indirectly),
 * 
 * <code>
 * DynScope.contains("fire_events");
 * </code>
 * 
 * will return true.
 * <p>
 * You can pass any kind of Object, not just a string as in the example. There
 * are also variants of runWith() that allow you to pass more than one Object at
 * once.
 * <p>
 * Also, you may pass an instance of the special class {@link DynScope.Tuple} as
 * a data object. Such an instance contains a "key" and a "value" (both
 * Objects). For example:
 * 
 * <code>
 * DynScope.runWith(new DynScope.Tuple("event_priority", "important"), new Runnable() {
 *    
 *    public void run() {
 *        // ... arbitrary code here .. call other methods etc.
 *    }
 * }
 * </code>
 * 
 * If you've done that, you can call
 * 
 * <code>
 * String prio = (String)DynScope.get("event_priority");
 * </code>
 * 
 * in run() or anywhere down the callstack and get back "important". (you could
 * also have called DynScope.contains("event_priority"), which would return
 * true)
 * 
 * @author Olaf Klischat
 */
public class DynScope {

    private static final ThreadLocal<Map<Object, Object>> perThreadValues = new ThreadLocal<Map<Object, Object>>() {
        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap<Object, Object>();
        }
    };

    public static void runWithValues(Runnable r, Object... values) {
        Set<Object> newEntries = new HashSet<Object>();
        for (Object v: values) {
            if (v instanceof Tuple) {
                Tuple t = (Tuple)v;
                if (null == perThreadValues.get().put(t.getKey(), t.getValue())) {
                    newEntries.add(t.getKey());
                }
            } else {
                if (null == perThreadValues.get().put(v, 1)) {
                    newEntries.add(v);
                }
            }
        }
        try {
            r.run();
        } finally {
            for (Object e: newEntries) {
                perThreadValues.get().remove(e);
            }
        }
    }
    
    
    public static class Tuple {
        private Object k, v;
        public Tuple(Object k, Object v) {
            this.k = k;
            this.v = v;
        }
        public Object getKey() {
            return k;
        }
        public Object getValue() {
            return v;
        }
    }
    
    
    //convenience methods
    
    public static void runWith(Object v1, Runnable r) {
        runWithValues(r, v1);
    }

    public static void runWith(Object v1, Object v2, Runnable r) {
        runWithValues(r, v1, v2);
    }

    public static void runWith(Object v1, Object v2, Object v3, Runnable r) {
        runWithValues(r, v1, v2, v3);
    }

    // getter / tester methods
    
    public static Object get(Object key) {
        return perThreadValues.get().get(key);
    }
    
    public static boolean contains(Object value) {
        return get(value) != null;
    }
    
    
    // TODO: maybe use Object lists as values and allow multiple pieces of code along
    //  the callstack to independently add objects to the same key
}

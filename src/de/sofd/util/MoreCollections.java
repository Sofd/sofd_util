package de.sofd.util;

import de.sofd.lang.Function1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provide static utility methods dealing with collections, just like
 * java.util.Collections does.
 * <p>
 * This should be named "Collections" and derived from java.util.Collections,
 * but the latter doesn't allow extending (its constructor is private).
 * <p>
 * RFE has been submitted to bugs.sun.com; see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6588768 :-\
 * @author olaf
 */
public class MoreCollections {

    //this is what java.util.Collections should do as well...
    protected MoreCollections() {
        throw new IllegalStateException("not instantiable");
    }
    
    /**
     * 
     * @param <T>
     * @param input input
     * @param filter filter
     * @return iterator over all elements of input for which filter holds true
     */
    public static <T> Iterator<T> filteredIterator(final Iterator<? extends T> input, final Predicate<T> filter) {
        return new Iterator<T>() {
            private boolean hasNext = true;
            private T nextOutput;
            private void advanceToNext() {
                while (input.hasNext()) {
                    T t = input.next();
                    if (filter.holdsFor(t)) {
                        nextOutput = t;
                        return;
                    }
                }
                hasNext = false;
            }

            {
                advanceToNext();
            }
            
            @Override
            public boolean hasNext() {
                return hasNext;
            }
            @Override
            public T next() {
                if (!hasNext) {
                    throw new NoSuchElementException();
                }
                T result = nextOutput;
                advanceToNext();
                return result;
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * 
     * @param <T>
     * @param input input
     * @param filter filter
     * @return Iterable containing all elements of input for which filter holds true
     */
    public static <T> Iterable<T> filteredIterable(final Iterable<T> input, final Predicate<T> filter) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return filteredIterator(input.iterator(), filter);
            }
        };
    }

    /**
     * Same as {@link #filteredIterable(Iterable, Predicate)}, but for collections. Needed
     * when you need to work with collections instead of (more general) iterables.
     * @param <T>
     * @param input input
     * @param filter filter
     * @return Collection containing all elements of input for which filter holds true
     */
    public static <T> Collection<T> filteredCollection(final Collection<? extends T> input, final Predicate<T> filter) {
        return new Collection<T>() {
            private Collection<T> backend = new ArrayList<T>();
            {
                for (T elt: iterableContaining(filteredIterator(input.iterator(), filter))) {
                    backend.add(elt);
                }
            }
            
            @Override
            public Iterator<T> iterator() {
                return backend.iterator();
            }

            @Override
            public boolean add(T e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends T> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean contains(Object o) {
                return backend.contains(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return backend.containsAll(c);
            }

            @Override
            public boolean isEmpty() {
                return backend.isEmpty();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return backend.size();
            }

            @Override
            public Object[] toArray() {
                return backend.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return backend.toArray(a);
            }

            /**
            //doesn't work b/c of "type erasure"
            @Override
            public <T2 extends T> T2[] toArray(T2[] a) {
                return backend.toArray(a);
            }
            */
        };
    }

    
    public static <T> Iterable<T> iterableContaining(final Iterator<T> iterator) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }



    /**
     * 
     * @param <T>
     * @param <T2>
     * @param input any iterator
     * @param mapFn function to be applied to each element of input
     * @return iterator over the results of applying mapFn to the elements of input
     */
    public static <T, T2> Iterator<T2> mappedIterator(final Iterator<? extends T> input, final Function1<T, T2> mapFn) {
        return new Iterator<T2>() {
            @Override
            public boolean hasNext() {
                return input.hasNext();
            }
            @Override
            public T2 next() {
                return mapFn.run(input.next());
            }
            @Override
            public void remove() {
                input.remove();
            }
        };
    }


    /**
     *
     * @param <T>
     * @param <T2>
     * @param input any Iterable
     * @param mapFn function to be applied to each element of input
     * @return Iterable containing the results of applying mapFn to the elements of input
     */
    public static <T, T2> Iterable<T2> mappedIterable(final Iterable<T> input, final Function1<T, T2> mapFn) {
        return new Iterable<T2>() {
            @Override
            public Iterator<T2> iterator() {
                return mappedIterator(input.iterator(), mapFn);
            }
        };
    }

    /**
     * 
     * @param <T>
     * @param <T2>
     * @param input any collection
     * @param mapFn function to be applied to each element of input
     * @return collection containing the results of applying mapFn to the elements of input
     */
    public static <T, T2> Collection<T2> mappedCollection(final Collection<? extends T> input, final Function1<T, T2> mapFn) {
        return new Collection<T2>() {
            private Collection<T2> backend = new ArrayList<T2>();
            {
                for (T2 elt: iterableContaining(mappedIterator(input.iterator(), mapFn))) {
                    backend.add(elt);
                }
            }

            @Override
            public Iterator<T2> iterator() {
                return backend.iterator();
            }

            @Override
            public boolean add(T2 e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends T2> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean contains(Object o) {
                return backend.contains(o);
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return backend.containsAll(c);
            }

            @Override
            public boolean isEmpty() {
                return backend.isEmpty();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
                return backend.size();
            }

            @Override
            public Object[] toArray() {
                return backend.toArray();
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return backend.toArray(a);
            }

            /**
            //doesn't work b/c of "type erasure"
            @Override
            public <T2 extends T> T2[] toArray(T2[] a) {
                return backend.toArray(a);
            }
            */
        };
    }
}

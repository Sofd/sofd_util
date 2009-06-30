package de.sofd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Misc {

    private Misc() {}
    
    public static <T> T deepCopy(T t) {
        try {
            ByteArrayOutputStream serializedT = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedT);
            oos.writeObject(t);
            oos.flush();
            return (T)(new ObjectInputStream(new ByteArrayInputStream(serializedT.toByteArray())).readObject());
        } catch (NotSerializableException e) {
            throw new RuntimeException("serialization error",e);
        } catch (IOException e) {
            throw new IllegalStateException("should never happen",e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("should never happen",e);
        }
    }

    
    public static boolean equal(Object o1, Object o2) {
        return ((o1==null) && (o2==null)) ||
               ((o1!=null) && (o1.equals(o2))) ||
               ((o2!=null) && (o2.equals(o1)));
    }
    
    
    public static <T> Predicate<T> predAnd(final Predicate<T> p1, final Predicate<T> p2) {
        return new Predicate<T>() {
            @Override
            public boolean holdsFor(T x) {
                return p1.holdsFor(x) && p2.holdsFor(x);
            }
        };
    }
}

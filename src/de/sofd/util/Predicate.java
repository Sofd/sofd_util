package de.sofd.util;

public interface Predicate<ArgumentType> {
    public boolean holdsFor(ArgumentType x);
}

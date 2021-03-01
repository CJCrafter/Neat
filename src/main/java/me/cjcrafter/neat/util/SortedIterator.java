package me.cjcrafter.neat.util;

import java.util.Iterator;

public interface SortedIterator<E> extends Iterator<E> {

    void insert(E element);

    @Override
    void remove();
}

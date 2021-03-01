package me.cjcrafter.neat;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

public class SortedList<E> extends AbstractList<E> implements List<E>, RandomAccess {

    private final Comparator<E> comparator;
    private final Class<E> clazz;

    private E[] arr;
    private int size;

    public SortedList(Comparator<E> comparator, Class<E> clazz) {
        this.comparator = comparator;
        this.clazz = clazz;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size)
            throw new ArrayIndexOutOfBoundsException("For index " + index + " in size " + size);

        return arr[index];
    }

    @Override
    public int size() {
        return size;
    }
}

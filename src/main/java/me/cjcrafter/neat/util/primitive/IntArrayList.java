package me.cjcrafter.neat.util.primitive;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class IntArrayList implements IntList {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int[] EMPTY = {};

    private int[] arr;
    private int size;

    public IntArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public IntArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.arr = new int[initialCapacity];
        } else if (initialCapacity == 0) {
            this.arr = EMPTY;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    public IntArrayList(IntCollection collection) {
        if (collection.size() == 0)
            this.arr = EMPTY;
        else
            this.arr = Arrays.copyOf(collection.toArray(), collection.size());
    }

    private void ensureCapacity(int min) {
        if (min - arr.length > 0) {
            int oldCap = arr.length;
            int newCap = oldCap + (oldCap >> 1);
            if (newCap - min < 0)
                newCap = min;
            if (newCap - (Integer.MAX_VALUE - 8) > 0) {
                if (min < 0)
                    throw new OutOfMemoryError();
                newCap = min > (Integer.MAX_VALUE - 8) ? Integer.MAX_VALUE : (Integer.MAX_VALUE - 8);
            }

            arr = Arrays.copyOf(arr, newCap);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            if (arr[i] == value)
                return true;
        }
        return false;
    }

    @Override
    public boolean add(int value) {
        ensureCapacity(size + 1);
        arr[size++] = value;
        return true;
    }

    @Override
    public boolean remove(int value) {
        removeIndex(indexOf(value));
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            arr[i] = 0;
        }
        size = 0;
    }

    @Override
    public int[] toArray() {
        return Arrays.copyOf(arr, size);
    }

    @Override
    public int get(int index) {
        return arr[index];
    }

    @Override
    public int set(int index, int value) {
        int old = arr[index];
        arr[index] = value;
        return old;
    }

    @Override
    public void add(int index, int value) {
        ensureCapacity(size + 1);
        System.arraycopy(arr, index, arr, index + 1, size - index);
        arr[index] = value;
        size++;
    }

    @Override
    public int removeIndex(int index) {
        int old = arr[index];

        int shiftElements = size - index - 1;
        if (shiftElements > 0)
            System.arraycopy(arr, index + 1, arr, index, shiftElements);
        arr[--size] = 0;

        return old;
    }

    @Override
    public int indexOf(int value) {
        for (int i = 0; i < size; i++) {
            if (arr[i] == value)
                return i;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(int value) {
        for (int i = size - 1; i >= 0; i--) {
            if (arr[i] == value)
                return i;
        }

        return -1;
    }

    @Override
    public IntListIterator listIterator() {
        return new ArrayListIterator(0);
    }

    @Override
    public IntListIterator listIterator(int index) {
        return new ArrayListIterator(index);
    }

    private class ArrayIterator implements IntIterator {

        int cursor;
        int last = -1;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public int next() {
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();

            cursor = i + 1;
            return arr[last = i];
        }

        @Override
        public void remove() {
            if (last < 0)
                throw new IllegalStateException();

            IntArrayList.this.removeIndex(last);
            cursor = last;
            last = -1;
        }
    }

    private class ArrayListIterator extends ArrayIterator implements IntListIterator {

        ArrayListIterator(int index) {
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            cursor = i;
            return arr[last = i];
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void set(int value) {
            if (last < 0)
                throw new IllegalStateException();

            IntArrayList.this.set(last, value);
        }

        @Override
        public void add(int value) {
            int i = cursor;
            IntArrayList.this.add(i, value);
            cursor = i + 1;
            last = -1;
        }
    }
}

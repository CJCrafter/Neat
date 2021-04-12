package me.cjcrafter.neat.util.primitive;

public class IntArrayList implements IntList {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int[] EMPTY = {};

    private int[] arr;
    private int size;

    public IntArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.arr = new int[initialCapacity];
        } else if (initialCapacity == 0) {
            this.arr = EMPTY;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
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
        return false;
    }

    @Override
    public boolean remove(int value) {
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            arr[i] = 0;
        }
        size = 0;
    }

    @Override
    public int get(int index) {
        return 0;
    }

    @Override
    public int set(int index, int value) {
        return 0;
    }

    @Override
    public void add(int index, int value) {

    }

    @Override
    public int removeIndex(int index) {
        return 0;
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
        return null;
    }

    @Override
    public IntListIterator listIterator(int index) {
        return null;
    }
}

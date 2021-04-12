package me.cjcrafter.neat.util.primitive;

public interface IntList extends IntCollection {

    int get(int index);

    int set(int index, int value);

    void add(int index, int value);

    int removeIndex(int index);

    int indexOf(int value);

    int lastIndexOf(int value);

    IntListIterator listIterator();

    IntListIterator listIterator(int index);
}

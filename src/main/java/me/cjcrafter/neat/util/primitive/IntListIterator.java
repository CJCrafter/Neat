package me.cjcrafter.neat.util.primitive;

public interface IntListIterator extends IntIterator {

    boolean hasPrevious();

    int previous();

    int nextIndex();

    int previousIndex();

    void set(int value);

    void add(int value);
}

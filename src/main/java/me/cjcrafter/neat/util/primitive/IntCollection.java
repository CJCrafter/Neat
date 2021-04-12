package me.cjcrafter.neat.util.primitive;

public interface IntCollection {

    int size();

    boolean isEmpty();

    boolean contains(int value);

    boolean add(int value);

    boolean remove(int value);

    void clear();
}

package me.cjcrafter.neat.util.primitive;

public interface DoubleCollection extends DoubleIterable {

    int size();

    boolean isEmpty();

    boolean contains(Object other);

    boolean add(double num);

    boolean remove(double num);

    void clear();
}

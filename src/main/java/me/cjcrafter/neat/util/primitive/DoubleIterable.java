package me.cjcrafter.neat.util.primitive;

import java.util.function.DoubleConsumer;

public interface DoubleIterable {

    DoubleIterator iterator();

    default void forEach(DoubleConsumer consumer) {
        DoubleIterator iterator = iterator();

        while (iterator.hasNext())
            consumer.accept(iterator.next());
    }
}

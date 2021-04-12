package me.cjcrafter.neat.util.primitive;

import java.util.function.IntConsumer;

public interface IntIterable {

    IntIterator iterator();

    default void forEach(IntConsumer consumer) {
        IntIterator iterator = iterator();

        while (iterator.hasNext())
            consumer.accept(iterator.next());
    }
}

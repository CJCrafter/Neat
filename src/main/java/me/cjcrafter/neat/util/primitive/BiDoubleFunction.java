package me.cjcrafter.neat.util.primitive;

/**
 * This interface outlines some action that accepts a generic first argument
 * and a double second argument, and returns a generic return type. This
 * interface has the advantage of not needing to wrap and unwrap a
 * {@link Double}.
 *
 * @param <T>
 * @param <U>
 * @see DoubleMap#removeAll(BiDoubleFunction)
 */
@FunctionalInterface
public interface BiDoubleFunction<T, U> {
    U apply(T t, double num);
}

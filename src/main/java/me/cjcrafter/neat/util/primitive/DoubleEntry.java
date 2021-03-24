package me.cjcrafter.neat.util.primitive;

import java.util.Objects;

public abstract class DoubleEntry<K> {

    public abstract K getKey();

    public abstract double getValue();

    public abstract double setValue(double value);

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        else if (other == null)
            return false;
        else if (!(other instanceof DoubleEntry))
            return false;

        DoubleEntry<?> entry = (DoubleEntry<?>) other;
        return Objects.equals(getKey(), entry.getKey()) && Objects.equals(getValue(), entry.getValue());
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}

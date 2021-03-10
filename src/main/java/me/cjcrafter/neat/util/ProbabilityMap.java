package me.cjcrafter.neat.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.function.ToDoubleFunction;

public class ProbabilityMap<E> extends AbstractCollection<E> implements Set<E> {

    private static class Node<E> {

        E item;
        private double weight;
        private double offset;
        Node<E> next;

        Node() {
        }

        Node(E item, double weight, double offset) {
            this.item = item;
            this.weight = weight;
            this.offset = offset;
        }

        public double getWeight() {
            return weight;
        }

        public double setWeight(double weight) {
            double diff = weight - this.weight;
            modifyWeight(diff);
            return diff;
        }

        private void modifyWeight(double diff) {
            this.weight += diff;

            if (next != null) {
                next.modifyWeight(diff);
            }
        }

        public double getOffset() {
            return offset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return item.equals(node.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }

    private final SplittableRandom random = new SplittableRandom();
    private final TreeMap<Node<E>, Node<E>> map;
    private final Node<E> dummy;
    private double total;

    public ProbabilityMap() {
        map = new TreeMap<>(Comparator.comparingDouble(Node::getOffset));
        dummy = new Node<>();
    }

    // Probability Operations

    public void put(E item, double chance) {
        if (item == null)
            throw new IllegalArgumentException();

        dummy.item = item;

        if (map.containsKey(dummy)) {
            Node<E> node = map.get(dummy);
            total += node.setWeight(chance);
        } else {
            Node<E> node = new Node<>(item, chance, total);
            node.next = map.ceilingKey(node);
            map.put(node, node);
            total += chance;
        }
    }

    public <T extends E> void putAll(Collection<T> items, ToDoubleFunction<T> function) {
        for (T item : items) {
            double chance = function.applyAsDouble(item);
            put(item, chance);
        }
    }

    public E get() {
        if (isEmpty())
            throw new NoSuchElementException();

        dummy.offset = random.nextDouble(total);
        return map.floorKey(dummy).item;
    }

    // Set Operations

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @SuppressWarnings("all")
    @Override
    public boolean contains(Object o) {
        dummy.item = (E) o;
        return map.containsKey(dummy.item);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Use put()");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        dummy.item = (E) o;
        return map.remove(dummy.item) != null;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Use putAll()");
    }

    @Override
    public void clear() {
        map.clear();
    }

    // Iterators

    @Override
    public Iterator<E> iterator() {
        return new IteratorWrapper();
    }

    private class IteratorWrapper implements Iterator<E> {
        private final Iterator<Node<E>> iterator;

        public IteratorWrapper() {
            iterator = map.keySet().iterator();
        }

        @Override public boolean hasNext() { return iterator.hasNext(); }
        @Override public E next() { return iterator.next().item; }
        @Override public void remove() { iterator.remove(); }
    }
}

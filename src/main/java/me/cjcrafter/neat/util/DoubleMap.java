package me.cjcrafter.neat.util;

import me.cjcrafter.neat.file.Serializable;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DoubleMap<K> implements Serializable {

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static class Node<K> {

        K key;
        double value;
        int hash;

        Node<K> next;

        public Node(K key, double value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }

        void set(double value, int hash) {
            this.value = value;
            this.hash = hash;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private Node<K>[] table;
    private final float loadFactor;
    private int threshold;
    private int size;

    public DoubleMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    // Internal mapping methods

    private int hash(Object key) {
        return key.hashCode();
    }

    private Node<K> getNode(Object key) {
        Node<K> node = table[hash(key) & (threshold - 1)];

        if (node != null) {
            do {
                if (Objects.equals(node.key, key))
                    return node;
            } while ((node = node.next) != null);
        }

        return null;
    }

    private void resize() {
        Node<K>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings("unchecked")
        Node<K>[] newTab = (Node<K>[]) new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K> node;
                if ((node = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (node.next == null)
                        newTab[node.hash & (newCap - 1)] = node;
                    else {
                        Node<K> loHead = null, loTail = null;
                        Node<K> hiHead = null, hiTail = null;
                        Node<K> next;
                        do {
                            next = node.next;
                            if ((node.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = node;
                                else
                                    loTail.next = node;
                                loTail = node;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = node;
                                else
                                    hiTail.next = node;
                                hiTail = node;
                            }
                        } while ((node = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
    }

    // Public Operations

    public int size() {
        return size;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean contains(Object key) {
        return getNode(key) != null;
    }

    public double get(K key) {
        Node<K> node = getNode(key);
        return node == null ? 0.0 : node.value;
    }

    public double put(K key, double value) {
        if (table == null)
            resize();

        int hash = hash(key);
        int index = hash & (table.length - 1);
        Node<K> node = table[index];
        if (node == null)
            table[index] = new Node<>(key, value, hash);
        else {
            Node<K> temp;
            if (node.hash == hash && Objects.equals(key, node.hash))
                temp = node;
            else {
                while (true) {
                    if ((temp = node.next) == null) {
                        node.next = new Node<>(key, value, hash);
                        break;
                    }
                    if (temp.hash == hash && Objects.equals(key, temp.key))
                        break;

                    node = temp;
                }
            }
            if (temp != null) {
                double old = temp.value;
                temp.value = value;
                return old;
            }
        }

        if (++size > threshold)
            resize();
        return 0.0;
    }

    // Iterators

    private abstract class HashIterator {
        Node<K> next;
        Node<K> current;
        int index;

        HashIterator() {
            current = next = null;
            index = 0;
            if (table != null && size > 0) { // advance to first entry
                do {} while (index < table.length && (next = table[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node<K> nextNode() {
            Node<K> node = next;
            if (node == null)
                throw new NoSuchElementException();
            if ((next = (current = node).next) == null && table != null) {
                do {} while (index < table.length && (next = table[index++]) == null);
            }
            return node;
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<K> {
        public final K next() { return nextNode().key; }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<Double> {
        public final Double next() { return nextNode().value; }
    }

    final class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K, Double>> {

        public final Map.Entry<K, Double> next() {
            return new Map.Entry<K, Double>() {
                final Node<K> node = nextNode();
                @Override public K getKey()                    { return node.key; }
                @Override public Double getValue()             { return node.value; }
                @Override public Double setValue(Double value) { throw new UnsupportedOperationException(); }
            };
        }
    }

    // File methods

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = new JSONObject();

        EntryIterator iterator = new EntryIterator();
        while (iterator.hasNext()) {
            Map.Entry<K, Double> entry = iterator.next();
            json.put(entry.getKey(), entry.getValue());
        }

        return json;
    }

    @Override
    public void serialize(JSONObject data) {

    }
}
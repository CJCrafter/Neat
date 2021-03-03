package me.cjcrafter.neat.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

public class SortedList<E> extends AbstractSet<E> {

    protected static class Node<E> {

        E key;
        final int hash;
        Node<E> before, after;

        Node(E key, int hash) {
            this.key = key;
            this.hash = hash;
        }

        public E getKey()                  { return key; }
        @Override public String toString() { return key.toString(); }
        @Override public int hashCode()    { return hash; }
    }

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private final Node<E>[] table;
    private final int threshold;
    private int size;
    private Node<E> head, tail;

    @SuppressWarnings("unchecked")
    public SortedList(int capacity) {

        // Ensure we have a multiple of 2 as a maximum size
        if ((capacity & (capacity - 1)) != 0) {
            capacity = tableSizeFor(capacity);
        }

        this.table = (Node<E>[]) new Node[capacity];
        this.threshold = capacity;
    }

    // Public operations

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public int getThreshold() {
        return threshold;
    }

    public E get(E element) {
        Node<E> node = getNode(element);
        return node == null ? null : node.key;
    }

    public E get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Illegal index: " + index);

        Node<E> node = head;
        for (int i = 0; i < index; i++) {
            node = node.after;
        }

        return node.key;
    }

    public E getHead() {
        return head == null ? null : head.key;
    }

    public E getTail() {
        return tail == null ? null : tail.key;
    }

    @Override
    public boolean contains(Object o) {
        return o != null && getNode(o) != null;
    }

    @Override
    public boolean add(E element) {
        if (element == null)
            throw new IllegalArgumentException("Element cannot be null");

        int hash = element.hashCode();
        validateHash(hash);
        Node<E> node = table[hash];

        if (node == null) {
            node = newNode(element, hash);
            linkLast(node);
            size++;
            table[hash] = node;
            return true;
        } else {
            return false;
        }
    }

    public void insert(E node, E element) {
        Node<E> n = getNode(node);
        if (n == null)
            throw new IllegalArgumentException("Unknown node: " + node);

        insertNode(n, element);
    }

    @Override
    public boolean remove(Object other) {
        Node<E> node = getNode(other);
        if (node != null) {
            table[node.hash] = null;
            size--;
            unlinkNode(node);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        if (table != null && size > 0) {
            size = 0;
            Arrays.fill(table, null);
            head = tail = null;
        }
    }

    public E getRandomElement() {
        if (size == 0)
            return null;

        return null;
    }

    // Internal operations

    private Node<E> newNode(E element, int hash) {
        return new Node<>(element, hash);
    }

    private void validateHash(int hash) {
        if (hash < 0 || hash >= threshold)
            throw new IllegalHashException("Illegal hash: " + hash + ", threshold: " + threshold);
    }

    private void linkLast(Node<E> node) {
        Node<E> last = tail;
        tail = node;

        if (last == null) {
            head = node;
        } else {
            last.after = node;
            node.before = last;
        }
    }

    private Node<E> getNode(Object key) {
        int hash = key.hashCode();
        if (hash < 0 || hash >= threshold)
            return null;
        else
            return table[hash];
    }

    private void insertNode(Node<E> node, E element) {
        int hash = element.hashCode();
        validateHash(hash);
        Node<E> temp = table[hash];
        Node<E> after = node.after;

        if (temp == null) {
            table[hash] = temp = newNode(element, hash);
            size++;

            temp.before = node;
            node.after = temp;
            if (after != null) {
                temp.after = after;
                after.before = temp;
            } else {
                tail = temp;
            }
        } else {
            throw new IllegalHashException("Duplicate hash: " + element);
        }
    }

    private void unlinkNode(Node<E> node) {
        Node<E> before = node.before, after = node.after;
        node.before = node.after = null;
        if (before == null)
            head = after;
        else
            before.after = after;
        if (after == null)
            tail = before;
        else
            after.before = before;
    }

    private void removeNode(Node<E> node) {
        table[node.hash] = null;
        size--;
        unlinkNode(node);
    }

    // Iterators

    @Override
    public SortedIterator<E> iterator() {
        return new LinkedIterator();
    }

    private class LinkedIterator implements SortedIterator<E> {

        private Node<E> current;
        private Node<E> next;

        LinkedIterator() {
            next = head;
        }

        @Override
        public void insert(E element) {
            if (current == null) {
                throw new IllegalStateException();
            }

            SortedList.this.insertNode(current, element);
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        public final Node<E> nextNode() {
            Node<E> next = this.next;
            if (next == null) {
                throw new NoSuchElementException();
            }

            this.current = next;
            this.next = next.after;
            return next;
        }

        @Override
        public E next() {
            return nextNode().key;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }

            SortedList.this.removeNode(current);
        }
    }
}

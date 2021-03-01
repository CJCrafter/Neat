package me.cjcrafter.neat.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SortedList<E> extends AbstractSet<E> {

    protected static class Node<E> implements Map.Entry<E, E> {

        E key;
        final int hash;
        Node<E> before, after;

        Node(E key, int hash) {
            this.key = key;
            this.hash = hash;
        }

        @Override public E getKey()          { return key; }
        @Override public E getValue()        { return key; }
        @Override public E setValue(E value) { throw new UnsupportedOperationException(); }
        @Override public String toString()   { return key.toString(); }
        @Override public int hashCode()      { return Objects.hash(key); }
    }

    private static final int INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float LOAD_FACTOR = 0.75f;

    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private static int hash(Object key) {
        int hash;
        return key == null ? 0 : (hash = key.hashCode()) ^ (hash >>> 16);
    }


    private Node<E>[] table;
    private Node<E> head, tail;
    private int size;
    private int threshold;

    public SortedList() {
    }

    public SortedList(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        else if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;

        this.threshold = tableSizeFor(initialCapacity);
    }

    // Public operations

    @Override
    public int size() {
        return size;
    }

    public int maxHashCode() {
        return table.length - 1;
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
        if (table == null || table.length == 0)
            table = resize();

        Node<E> node;
        int length = table.length;
        int hash = hash(element);
        int i = (length - 1) & hash;

        if ((node = table[i]) == null) {
            node = new Node<>(element, hash);
            linkLast(node);
            table[i] = node;
        } else if (node.hash == hash && (node.key.equals(element))) {
            node.key = element;
            return true;
        } else {
            throw new DuplicateHashException();
        }

        if (++size > threshold)
            resize();
        return true;
    }

    public void insert(E node, E element) {
        Node<E> n = getNode(node);
        if (n == null) {
            throw new IllegalArgumentException("Unknown node: " + node);
        }

        insertNode(n, element);
    }

    @Override
    public boolean remove(Object other) {
        int hash = other.hashCode();
        Node<E> node;
        int length, index;
        if (table != null && (length = table.length) > 0 && (node = table[index = (length - 1) & hash]) != null) {
            if (node.hash == hash && node.key.equals(other)) {
                table[index] = null;
            }
            size--;
            unlinkNode(node);

            return true;
        }
        return false;
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
        return null;
    }

    @Override
    public String toString() {
        return Arrays.toString(table);
    }

    // Internal operations

    private Node<E>[] resize() {
        Node<E>[] oldTable = table;
        int oldCap = (oldTable == null) ? 0 : oldTable.length;
        int oldThr = threshold;
        int newCap, newThr = 0;

        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTable;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= INITIAL_CAPACITY) {
                newThr = oldThr << 1;
            }
        } else if (oldThr > 0) {
            newCap = oldThr;
        } else {
            newCap = INITIAL_CAPACITY;
            newThr = (int) (LOAD_FACTOR * INITIAL_CAPACITY);
        }

        if (newThr == 0) {
            float ft = (float) newCap * LOAD_FACTOR;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
        }

        @SuppressWarnings("unchecked")
        Node<E>[] newTab = (Node<E>[]) new Node[newCap];
        table = newTab;
        threshold = newThr;

        // Copy the old values to the new table
        if (oldTable != null) {
            for (int i = 0; i < oldCap; i++) {
                Node<E> node = oldTable[i];

                if (node == null) {
                    continue;
                }
                oldTable[i] = null;
                if (node.after == null) {
                    newTab[node.hash & (newCap - 1)] = node;
                    continue;
                }

                Node<E> loHead = null, loTail = null, hiHead = null, hiTail = null, next;
                do {
                    next = node.after;
                    if ((node.hash & oldCap) == 0) {
                        if (loTail == null)
                            loHead = node;
                        else
                            loTail.after = node;
                        loTail = node;
                    }
                    else {
                        if (hiTail == null)
                            hiHead = node;
                        else
                            hiTail.after = node;
                        hiTail = node;
                    }
                } while ((node = next) != null);
                if (loTail != null) {
                    loTail.after = null;
                    newTab[i] = loHead;
                }
                if (hiTail != null) {
                    hiTail.after = null;
                    newTab[i + oldCap] = hiHead;
                }
            }
        }
        return newTab;
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
        Node<E> first, node;
        int length, hash = hash(key);

        if (table != null && (length = table.length) > 0 && (first = table[(length - 1) & hash]) != null) {
            if (first.hash == hash && first.key.equals(key)) {
                return first;
            }

            // This code should never be executed, testing needed
            if (true) throw new DuplicateHashException();
            if ((node = first.after) != null) {
                do {
                    if (node.hash == hash && node.key.equals(key))
                        return node;
                } while ((node = node.after) != null);
            }
        }
        return null;
    }

    private void insertNode(Node<E> node, E element) {
        int hash = hash(element);
        int index = (table.length - 1) & hash;

        Node<E> temp = table[index];
        Node<E> after = node.after;

        if (temp == null) {
            table[index] = temp = new Node<>(element, hash);
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
            throw duplicateHash(node, temp);
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
        int index = (table.length - 1) & node.hash;
        table[index] = null;
        unlinkNode(node);
    }

    private DuplicateHashException duplicateHash(Node<E> a, Node<E> b) {
        return new DuplicateHashException("For nodes " + a + "(" + a.hash + ") & " + b + "(" + b.hash + ")");
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

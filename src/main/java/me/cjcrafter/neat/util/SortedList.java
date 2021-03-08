package me.cjcrafter.neat.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SortedList<E> extends AbstractSet<E> implements Set<E> {

    protected static class Node<E> {

        final E key;
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
    private Comparator<E> comparator;

    @SuppressWarnings("unchecked")
    public SortedList(int capacity) {

        // Ensure we have a multiple of 2 as a maximum size
        if ((capacity & (capacity - 1)) != 0) {
            capacity = tableSizeFor(capacity);
        }

        this.table = (Node<E>[]) new Node[capacity];
        this.threshold = capacity;
    }

    @SuppressWarnings({"unchecked", "CopyConstructorMissesField"})
    public SortedList(SortedList<E> other) {
        this.table = (Node<E>[]) new Node[other.size];
        this.threshold = other.threshold;
        this.comparator = other.comparator;

        this.addAll(other);
    }

    // This method tests if some operation failed internally
    public void validate() {
        for (E e : this) {
            if (!this.contains(e)) {
                System.out.println(" !!! LinkedList contains element, but table does not: " + e);
            }
        }
        for (Node<E> node : table) {
            if (node == null) continue;
            boolean contains = false;

            for (E e : this) {
                if (node.hash == e.hashCode()) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                System.out.println(" !!! table contains element, but LinkedList does not: " + node.key);
        }
    }

    public void checkLinks() {
        if (isEmpty())
            return;

        Node<E> current, before, after;
        current = head;
        before = null;
        after = head.after;

        while (true) {

            if (current.before != before) {
                System.out.println(" !!! Missing before link for " + current);
            }

            if (after == null) {
                break;
            }

            before = current;
            current = after;
            after = after.after;
        }

        if (current != tail) {
            System.out.println(" !!! Tail node is unlinked. " + current);
        }
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

        int half = size / 2;

        // If the index is in the back half of the collection, then iterate
        // from the tail end. Otherwise iterate from the head.
        Node<E> node;
        if (index > half) {
            index = size - index - 1;
            node = tail;

            while (index-- > 0) {
                node = node.before;
            }
        } else {
            node = head;

            while (index-- > 0) {
                node = node.after;
            }
        }

        return node.key;
    }

    public boolean insertSorted(E element) {
        if (comparator == null)
            throw new IllegalStateException("No comparator has been set");

        int hash = element.hashCode();
        validateHash(hash);
        if (size == 0) {
            head = tail = newNode(element, element.hashCode());
            table[head.hash] = head;
            size++;
            return true;
        } else if (this.contains(element)) {
            return false;
        }

        Node<E> node = head;
        int compare;
        do {
            compare = comparator.compare(node.key, element);
        } while (compare < 0 && (node = node.after) != null);

        // Check if the element is on the tail first, then check if it is on
        // the head
        if (node == null) {
            Node<E> insert = newNode(element, hash);
            insert.before = tail;
            table[hash] = tail = tail.after = insert;
            size++;
        } else if (node.before == null) {
            Node<E> insert = newNode(element, hash);
            insert.after = head;
            table[hash] = head = head.before = insert;
            size++;
        } else {
            insertNode(node.before, element);
        }

        return true;
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
            removeNode(node);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        if (size > 0) {
            size = 0;
            Arrays.fill(table, null);
            head = tail = null;
        }
    }

    public E getRandomElement() {
        if (size == 0)
            return null;

        return get(ThreadLocalRandom.current().nextInt(size));
    }

    public void sort() {

    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
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

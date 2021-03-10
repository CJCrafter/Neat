package me.cjcrafter.neat.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SortedList<E> extends AbstractSet<E> implements Set<E>, Deque<E> {

    private static class Node<E> {

        Node(E item, int hash) {
            this.item = item;
            this.hash = hash;
        }

        Node(Node<E> before, E item, int hash, Node<E> after) {
            this.before = before;
            this.item = item;
            this.hash = hash;
            this.after = after;
        }

        E item;
        int hash;
        Node<E> before, after;

        public E get()                    { return item; }
        public void set(E item, int hash) { this.item = item; this.hash = hash; }
        public int hash()                 { return hash; }
        public String toString()          { return item.toString(); }
    }

    private final int threshold;
    private final Node<E>[] table;
    private Node<E> head, tail;
    private int size;

    private Comparator<E> comparator;

    @SuppressWarnings("unchecked")
    public SortedList(int threshold) {
        this.threshold = threshold;
        this.table = (Node<E>[]) new Node[threshold];
    }

    @SuppressWarnings({"unchecked", "CopyConstructorMissesField"})
    public SortedList(SortedList<E> other) {
        this.threshold = other.threshold;
        this.table = (Node<E>[]) new Node[threshold];
        this.comparator = other.comparator;

        this.addAll(other);
    }

    // Internal mapping methods

    private int hash(Object item) {
        return hash(item, false);
    }

    protected int hash(Object item, boolean suppress) {
        int hash = item.hashCode();

        if (!suppress && (hash < 0 || hash >= threshold))
            throw new IllegalHashException();

        return hash;
    }

    private Node<E> getNode(E e) {
        int hash = hash(e);
        return table[hash];
    }

    // Internal Linking methods

    private Node<E> linkFirst(E item) {
        Node<E> first = head;
        head = new Node<>(null, item, hash(item), first);

        if (first == null)
            tail = head;
        else
            first.before = head;

        table[head.hash] = head;
        size++;
        return head;
    }

    private Node<E> linkLast(E item) {
        Node<E> last = tail;
        tail = new Node<>(last, item, hash(item), null);

        if (last == null)
            head = tail;
        else
            last.after = tail;

        table[tail.hash] = tail;
        size++;
        return tail;
    }

    private Node<E> linkBefore(E item, Node<E> node) {
        Node<E> before = node.before;

        if (before == null)
            return linkFirst(item);

        Node<E> newNode = new Node<>(before, item, hash(item), node);
        before.after = node.before = newNode;

        table[newNode.hash] = newNode;
        size++;
        return newNode;
    }

    private E unlinkFirst() {
        if (size == 0)
            return null;

        Node<E> first = head;
        if (first.after == null)
            head = tail = null;
        else {
            head = first.after;
            first.after.before = null;
        }

        table[head.hash] = null;
        size--;
        return first.item;
    }

    private E unlinkLast() {
        if (size == 0)
            return null;

        Node<E> last = tail;
        if (last.before == null)
            head = tail = null;
        else {
            tail = last.before;
            last.before.after = null;
        }

        table[tail.hash] = null;
        size--;
        return last.item;
    }

    private E unlink(Node<E> node) {
        Node<E> before = node.before;
        Node<E> after = node.after;

        if (before == null)
            head = after;
        else {
            before.after = after;
            node.before = null;
        }

        if (after == null)
            tail = before;
        else {
            after.before = before;
            node.after = null;
        }

        table[node.hash] = null;
        size--;
        return node.item;
    }

    private Node<E> getNode(int index) {
        Node<E> node;

        int half = size >> 1;
        if (index < half) {
            node = head;
            for (int i = 0; i < index; i++)
                node = node.after;
        } else {
            node = tail;
            for (int i = size - 1; i > index; i--)
                node = node.before;
        }

        return node;
    }

    // Public Operations

    @Override
    public int size() {
        return size;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public boolean contains(Object o) {
        int hash = hash(o, true);
        if (hash < 0 || hash >= threshold)
            return false;
        else
            return table[hash] != null;
    }

    @Override
    public boolean add(E e) {
        if (contains(e))
            return false;

        linkLast(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int hash = hash(o);
        Node<E> node = table[hash];

        if (node == null)
            return false;

        unlink(node);
        return true;
    }

    @Override
    public void clear() {
        if (size != 0) {
            ListIterator<E> iterator = iterator();
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    public E get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("For index: " + index);

        return getNode(index).item;
    }

    public E get(E item) {
        Node<E> node = getNode(item);
        return node == null ? null : node.item;
    }

    public E getRandomElement() {
        if (size == 0)
            throw new NoSuchElementException();

        return get(ThreadLocalRandom.current().nextInt(size));
    }

    // Sorting Operations (Defaults to natural order, if applicable)

    public Comparator<E> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public void addSorted(E item) {
        Comparator<E> comparator = this.comparator;
        if (comparator == null)
            comparator = (Comparator<E>) Comparator.naturalOrder();

        if (isEmpty())
            linkFirst(item);
        else if (contains(item))
            return;

        Node<E> node = head;
        int compare;
        do {
            compare = comparator.compare(node.item, item);
        } while (compare < 0 && (node = node.after) != null);

        if (node == null)
            linkLast(item);
        else
            linkBefore(item, node);
    }

    @SuppressWarnings("unchecked")
    public void sort() {
        E[] arr = (E[]) new Object[size];
        int index = 0;
        for (E element : this) {
            arr[index++] = element;
        }

        Arrays.sort(arr);

        ListIterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            index = iterator.nextIndex();
            iterator.next();
            iterator.set(arr[index]);
        }
    }

    // Polling Operations (Deque)

    @Override
    public void addFirst(E e) {
        if (!contains(e))
            linkFirst(e);
        else
            throw new IllegalStateException(e + " already exists in this set");
    }

    @Override
    public void addLast(E e) {
        if (!contains(e))
            linkLast(e);
        else
            throw new IllegalStateException(e + "already exists in this set");
    }

    @Override
    public boolean offerFirst(E e) {
        if (contains(e))
            return false;

        linkFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        return add(e);
    }

    @Override
    public E removeFirst() {
        if (head == null)
            throw new NoSuchElementException();

        return unlinkFirst();
    }

    @Override
    public E removeLast() {
        if (tail == null)
            throw new NoSuchElementException();

        return unlinkLast();
    }

    @Override
    public E pollFirst() {
        return isEmpty() ? null : unlinkFirst();
    }

    @Override
    public E pollLast() {
        return isEmpty() ? null : unlinkLast();
    }

    @Override
    public E getFirst() {
        if (head == null)
            throw new NoSuchElementException();

        return head.item;
    }

    @Override
    public E getLast() {
        if (tail == null)
            throw new NoSuchElementException();

        return tail.item;
    }

    @Override
    public E peekFirst() {
        return isEmpty() ? null : head.item;
    }

    @Override
    public E peekLast() {
        return isEmpty() ? null : tail.item;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return isEmpty() ? null : unlinkFirst() ;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return isEmpty() ? null : head.item;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    // Iterators

    @Override
    public ListIterator<E> iterator() {
        return new SortedIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ReverseIterator();
    }

    private class SortedIterator implements ListIterator<E> {

        Node<E> last;
        Node<E> next;
        int nextIndex;

        private SortedIterator() {
            next = head;
        }

        private SortedIterator(int index) {
            next = (index == size) ? null : getNode(index);
            nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public E next() {
            last = next;
            next = next.after;
            nextIndex++;
            return last.item;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() {
            last = next = (next == null ? tail : next.before);
            nextIndex--;
            return last.item;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            unlink(last);
            last = null;
            nextIndex--;
        }

        @Override
        public void set(E e) {

            // Remove the old value from the hashtable, and add the new
            int hash = hash(e);
            table[last.hash] = null;
            table[hash] = last;

            last.set(e, hash);
        }

        @Override
        public void add(E e) {
            last = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);

            nextIndex++;
        }
    }

    private class ReverseIterator implements Iterator<E> {

        ListIterator<E> iterator;

        private ReverseIterator() {
            iterator = new SortedIterator(size);
        }

        @Override public boolean hasNext() { return iterator.hasPrevious(); }
        @Override public E next()          { return iterator.previous(); }
        @Override public void remove()     { iterator.remove(); }
    }
}

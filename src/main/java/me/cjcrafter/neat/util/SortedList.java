package me.cjcrafter.neat.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SortedList<E> extends AbstractSet<E> implements Set<E>, Deque<E> {

    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

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
        Node<E> _2d;

        public E get()                    { return item; }
        public void set(E item, int hash) { this.item = item; this.hash = hash; }
        public int hash()                 { return hash; }
        public String toString()          { return item.toString(); }
    }

    private Node<E>[] table;
    private Node<E> head, tail;
    private int threshold;
    private int size;
    private float loadFactor;

    private Comparator<E> comparator;

    public SortedList(int size) {
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = tableSizeFor(size);
    }

    @SuppressWarnings({"unchecked", "CopyConstructorMissesField"})
    public SortedList(SortedList<E> other) {
        this.threshold = other.threshold;
        this.table = (Node<E>[]) new Node[other.table.length];
        this.loadFactor = other.loadFactor;
        this.comparator = other.comparator;

        this.addAll(other);
    }

    // Internal mapping methods

    private int hash(Object item) {
        return item.hashCode();
    }

    private int tableSizeFor(int capacity) {
        int n = capacity - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private Node<E> getNode(Object item) {
        if (isEmpty())
            return null;

        int hash = hash(item);
        int index = hash & (table.length - 1);
        Node<E> node = table[index];

        if (node != null) {
            do {
                if (hash == node.hash && Objects.equals(node.item, item))
                    return node;
            } while ((node = node._2d) != null);
        }

        return null;
    }

    private Node<E> removeTable(E item) {
        if (isEmpty())
            return null;

        int hash = hash(item);
        int index = hash & (table.length - 1);
        Node<E> node = table[index];
        Node<E> before;

        if (node == null) {
            return null;
        } else if (hash == node.hash && Objects.equals(item, node.item)) {
            table[index] = node._2d;
            size--;
            return node;
        } else if ((before = node._2d) == null) {
            return null;
        } else {
            do {
                if (hash == node.hash && Objects.equals(node.item, item)) {
                    before._2d = node._2d;
                    size--;
                    return node;
                }

                before = node;
            } while ((node = node._2d) != null);
            return null;
        }
    }

    public void addTable(Node<E> node) {
        if (table == null)
            resize();

        int index = node.hash & (table.length - 1);
        Node<E> bucket = table[index];
        if (bucket == null)
            table[index] = node;
        else {
            Node<E> temp;
            if (bucket.hash == node.hash && Objects.equals(node.item, bucket.item))
                temp = node;
            else {
                while (true) {
                    if ((temp = bucket._2d) == null) {
                        bucket._2d = node;
                        break;
                    }
                    if (node.hash == temp.hash && Objects.equals(node.item, temp.item))
                        break;

                    bucket = temp;
                }
            }
            if (temp != null) {
                throw new UnsupportedOperationException("Node replacement");
            }
        }

        if (++size > threshold)
            resize();
    }

    private void resize() {
        Node<E>[] oldTab = table;
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
        Node<E>[] newTab = (Node<E>[]) new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<E> node;
                if ((node = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (node._2d == null)
                        newTab[node.hash & (newCap - 1)] = node;
                    else {
                        Node<E> loHead = null, loTail = null;
                        Node<E> hiHead = null, hiTail = null;
                        Node<E> next;
                        do {
                            next = node._2d;
                            if ((node.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = node;
                                else
                                    loTail._2d = node;
                                loTail = node;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = node;
                                else
                                    hiTail._2d = node;
                                hiTail = node;
                            }
                        } while ((node = next) != null);
                        if (loTail != null) {
                            loTail._2d = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail._2d = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
    }

    // Internal Linking methods

    private Node<E> linkFirst(E item) {
        Node<E> first = head;
        head = new Node<>(null, item, hash(item), first);

        if (first == null)
            tail = head;
        else
            first.before = head;

        addTable(head);
        return head;
    }

    private Node<E> linkLast(E item) {
        Node<E> last = tail;
        tail = new Node<>(last, item, hash(item), null);

        if (last == null)
            head = tail;
        else
            last.after = tail;

        addTable(tail);
        return tail;
    }

    private Node<E> linkBefore(E item, Node<E> node) {
        Node<E> before = node.before;

        if (before == null)
            return linkFirst(item);

        Node<E> newNode = new Node<>(before, item, hash(item), node);
        before.after = node.before = newNode;

        addTable(newNode);
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

        removeTable(first.item);
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

        removeTable(last.item);
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

        removeTable(node.item);
        return node.item;
    }

    private Node<E> getNode(int index) {
        Node<E> node;

        if (index < size >> 1) {
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
        return getNode(o) != null;
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
        Node<E> node = getNode(o);

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

        if (isEmpty()) {
            linkFirst(item);
            return;
        } else if (contains(item))
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

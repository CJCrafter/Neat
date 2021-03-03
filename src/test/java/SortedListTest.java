import me.cjcrafter.neat.util.SortedIterator;
import me.cjcrafter.neat.util.SortedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Comparator;

class SortedListTest {

    static SortedList<Integer> list_1 = new SortedList<>(16);
    static SortedList<Integer> list_2 = new SortedList<>(32);

    @BeforeAll
    static void fillLists() {
        Collections.addAll(list_1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.addAll(list_2, 1, 2, 4, 8, 16, 31);

        list_1.setComparator(Comparator.comparingInt(Integer::intValue));
        list_2.setComparator(Comparator.comparingInt(Integer::intValue));

        System.out.println(list_1);
        System.out.println(list_2);
    }

    @Test
    public void testData() {
        Assertions.assertEquals(list_1.size(), 10);
        Assertions.assertEquals(list_2.size(), 6);

        Assertions.assertEquals(list_1.getThreshold(), 16);
        Assertions.assertEquals(list_2.getThreshold(), 32);
    }

    @Test
    public void testHeadTail() {
        Assertions.assertEquals(list_1.getHead(), 0);
        Assertions.assertEquals(list_1.getTail(), 9);
        Assertions.assertEquals(list_2.getHead(), 1);
        Assertions.assertEquals(list_2.getTail(), 31);
    }

    @Test
    public void testInsertion() {
        testInsert(list_1, 0, 11, false);
        testInsert(list_1, 4, 12, false);
        testInsert(list_1, 9, 13, true);

        list_1.insertSorted(10);
        System.out.println(list_1);
        Assertions.assertEquals(list_1.get(10), 10);

        list_2.insertSorted(7);
        Assertions.assertEquals(list_2.get(3), 7);
    }

    @Test
    public void testContains() {
        Assertions.assertTrue(() -> list_1.contains(0));
        Assertions.assertTrue(() -> list_1.contains(3));
        Assertions.assertTrue(() -> list_1.contains(9));
        Assertions.assertTrue(() -> list_2.contains(1));
        Assertions.assertTrue(() -> list_2.contains(2));
        Assertions.assertTrue(() -> list_2.contains(31));

        Assertions.assertFalse(() -> list_1.contains(-9));
        Assertions.assertFalse(() -> list_1.contains(1000));
        Assertions.assertFalse(() -> list_1.contains(132));
        Assertions.assertFalse(() -> list_2.contains(-32));
        Assertions.assertFalse(() -> list_2.contains(1023));
        Assertions.assertFalse(() -> list_2.contains(47));
    }

    @Test
    public void testGet() {
        Assertions.assertEquals(list_1.get(0), 0);
        Assertions.assertEquals(list_1.get(2), 2);
        Assertions.assertEquals(list_1.get(4), 4);
        Assertions.assertEquals(list_1.get(5), 5);
        Assertions.assertEquals(list_1.get(6), 6);
        Assertions.assertEquals(list_1.get(9), 9);

        Assertions.assertEquals(list_2.get(0), 1);
        Assertions.assertEquals(list_2.get(1), 2);
        Assertions.assertEquals(list_2.get(2), 4);
        Assertions.assertEquals(list_2.get(3), 8);
        Assertions.assertEquals(list_2.get(5), 31);

    }

    private <E> void testInsert(SortedList<E> list, int index, E element, boolean expectError) {
        int oldIndex = index + 1;
        int size = list.size();
        SortedIterator<E> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();

            if (index-- == 0) {
                iterator.insert(element);
                break;
            }
        }

        Assertions.assertEquals(list.get(oldIndex), element);
        Assertions.assertEquals(list.size(), size + 1);

        list.remove(element);
        try {
            Assertions.assertNotEquals(list.get(oldIndex), element);
            if (expectError)
                throw new RuntimeException("Did not throw an exception");
        } catch (IndexOutOfBoundsException e) {
            if (!expectError)
                throw e;
        }
        Assertions.assertEquals(list.size(), size);
    }
}

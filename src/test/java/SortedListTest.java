import me.cjcrafter.neat.util.SortedIterator;
import me.cjcrafter.neat.util.SortedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SortedListTest {

    static SortedList<Integer> list_1 = new SortedList<>();
    static SortedList<Integer> list_2 = new SortedList<>(32);

    @BeforeAll
    static void fillLists() {
        Collections.addAll(list_1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.addAll(list_2, 1, 2, 4, 8, 16, 32);

        System.out.println(list_1);
        System.out.println(list_2);
    }

    @Test
    public void testStartSize() {
        Assertions.assertEquals(list_1.size(), 10);
        Assertions.assertEquals(list_2.size(), 6);
    }

    @Test
    public void testHeadTail() {
        Assertions.assertEquals(list_1.getHead(), 0);
        Assertions.assertEquals(list_1.getTail(), 9);
        Assertions.assertEquals(list_2.getHead(), 1);
        Assertions.assertEquals(list_2.getTail(), 32);
    }

    @Test
    public void testInsertion() {
        testInsert(list_1, 0, 11, false);
        testInsert(list_1, 4, 12, false);
        testInsert(list_1, 9, 13, true);
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
        } catch (IndexOutOfBoundsException e) {
            if (!expectError)
                throw e;
        }
        Assertions.assertEquals(list.size(), size);
    }
}

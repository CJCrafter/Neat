import me.cjcrafter.neat.SortList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

class SortListTest {

    static SortList<Integer> oddList = new SortList<>(Comparator.comparingInt(Integer::intValue), Integer.class);

    @BeforeAll
    static void fillLists() {
        fill(oddList, new Integer[]{-5, -3, 0, 1, 2, 3, 7, 10, 107});
    }

    private static <T> void fill(SortList<T> list, T[] arr) {
        list.addAll(Arrays.asList(arr));
    }

    @Test
    void testContains() {
        Assertions.assertTrue(() -> oddList.contains(1));
        Assertions.assertTrue(() -> oddList.contains(2));
        Assertions.assertTrue(() -> oddList.contains(-5));
        Assertions.assertTrue(() -> oddList.contains(107));
        Assertions.assertTrue(() -> oddList.contains(0));

        Assertions.assertTrue(() -> !oddList.contains(-1));
        Assertions.assertTrue(() -> !oddList.contains(103));
        Assertions.assertTrue(() -> !oddList.contains(-7));
    }

    @Test
    void testAddSorted() {
        testOneAddSorted(oddList, -10, 0);
        testOneAddSorted(oddList, -6, 0) ;
        testOneAddSorted(oddList, -5, 0) ;
        testOneAddSorted(oddList, -4, 1) ;
        testOneAddSorted(oddList, 0, 2)  ;
        testOneAddSorted(oddList, 4, 6)  ;
        testOneAddSorted(oddList, 11, 8) ;
        testOneAddSorted(oddList, 107, 8);
        testOneAddSorted(oddList, 108, 9);
    }

    private static <T> void testOneAddSorted(SortList<T> list, T element, int expectedIndex) {
        list.addSorted(element);
        Assertions.assertEquals(expectedIndex, list.indexOf(element));
        list.remove(element);
    }
}

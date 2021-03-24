import me.cjcrafter.neat.util.ProbabilityMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProbabilityMapTest {

    static ProbabilityMap<String> map;

    @BeforeAll
    static void fillMap() {
        map = new ProbabilityMap<>();
        map.put("Should occur 75%", 0.75);
        map.put("Should occur 20%", 0.20);
        map.put("Should occur 5%", 0.05);
    }

    @Test
    public void testProbabilities() {

        int bound = 10000;
        int[] occurrences = new int[map.size()];
        for (int i = 0; i < bound; i++) {
            switch (map.get()) {
                case "Should occur 75%":
                    occurrences[0]++;
                    break;
                case "Should occur 20%":
                    occurrences[1]++;
                    break;
                case "Should occur 5%":
                    occurrences[2]++;
                    break;
            }
        }

        isSimilar(occurrences[0] / (double) bound, 0.75);
        isSimilar(occurrences[1] / (double) bound, 0.20);
        isSimilar(occurrences[2] / (double) bound, 0.05);
    }

    private static void isSimilar(double a, double b) {
        double diff = Math.abs(a - b);
        Assertions.assertTrue(diff < 0.01, () -> "Numbers are not similar: " + a + " != " + b);
    }

}

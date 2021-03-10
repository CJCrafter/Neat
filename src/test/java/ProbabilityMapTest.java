import me.cjcrafter.neat.util.ProbabilityMap;
import org.junit.jupiter.api.BeforeAll;

public class ProbabilityMapTest {

    static ProbabilityMap<String> map;

    @BeforeAll
    static void fillMap() {
        map.put("75%", 0.75);
        map.put("20%", 0.20);
        map.put("5%", 0.05);
    }

}

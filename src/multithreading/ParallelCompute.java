package multithreading;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ParallelCompute {
    public static void main(String[] args) {
        int[] array = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14};
        Arrays.parallelPrefix(array, Integer::sum);
        System.out.println(Arrays.toString(array));
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>(Map.of(
                1,2,
                2,3,
                3,4,
                4,5
        ));
        int sum = map.reduceEntries(10,
                (e)-> e.getKey() + e.getValue(),
                (i, j)-> i*j
        );

        int sum1 = map.reduceEntries(10,
                (e)-> e.getKey() * e.getValue(),
                (i, j)-> i+j
        );

        System.out.println("SIGMA(PI)=" + sum1 + ", PI(SIGMA)=" + sum);
    }
}

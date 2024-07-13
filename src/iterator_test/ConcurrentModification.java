package iterator_test;

import java.util.HashMap;
import java.util.Map;

public class ConcurrentModification {
    private static final Map<Integer, Integer> map = new HashMap<>();
    public static void conditionalPut(int val, boolean condition) {
        if (condition) {
            map.put(10 + val, val * val);
            System.out.println("put");
        }
    }
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            map.put(i, i*i); // init
        }
        for (int val : map.values()) {
            conditionalPut(val, true);
        }
    }
}

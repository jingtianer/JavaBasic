package kollection;

import java.util.*;

public class KollectionDemo {
    public static void main(String[] args) {
        HashMap<Integer, String> map = new HashMap<>();
        for(int i = 0; i < 10; i++)
            map.put(i, Integer.toString(i));
        Set<Integer> keySet = map.keySet();
        Collection<String> valSet = map.values();
        Set<Map.Entry<Integer, String>> kvSet = map.entrySet();
        keySet.remove(4);
        valSet.remove("3");
//        kvSet.add(Map.entry(4, "3"));
        kvSet.remove(Map.entry(9, "9"));
        System.out.println(map);
    }
}

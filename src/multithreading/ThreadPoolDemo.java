package multithreading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class Tuple {
    private int[] ans = new int[3];
    public Tuple(int a, int b, int c) {
        ans[0] = a;
        ans[1] = b;
        ans[2] = c;
    }
    public int[] getAns() {
        return ans;
    }
}
public class ThreadPoolDemo {
    public static Tuple primeCnt(int start, int n) {
        if(start % 2 == 0) start++;
        int cnt = 0;
        for (int number = start; number < start+n; number+=2) {
            boolean isPrime = true;
            for(int j = 3; j <= Math.sqrt(number); j+=2) {
                if(number % j == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                System.out.println(number + " isPrime");
                cnt++;
            }
        }
        return new Tuple(start, n, cnt);
    }
    public static void main(String[] args) {
        List<Callable<Tuple>> tasks = new ArrayList<>(100);
        for(int i = 0; i < 100; i++) {
            final int start = i;
            tasks.add(()-> primeCnt(start, 100));
        }
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        ExecutorCompletionService<Tuple> service = new ExecutorCompletionService<>(executorService);
        for(var task : tasks) service.submit(task);
        for(int i = 0; i < tasks.size(); i++) {
            try {
                int[] res = service.take().get().getAns();
                System.out.println("res = " + Arrays.toString(res));
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ok!");
        executorService.shutdown(); // 必须shutdown
    }
}

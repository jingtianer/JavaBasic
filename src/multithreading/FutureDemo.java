package multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import static common.KotlinUtils.with;

public class FutureDemo {
    public static void main(String[] args) {
        Callable<Integer> task = () -> {
            int ans = 0;
            for(int i = 0; i < 10; i++) {
                ans += i;
                Thread.sleep(1000);
            }
            return ans;
        };
        FutureTask<Integer> futureTask = new FutureTask<>(task);
        with(new Thread(futureTask), (thread) -> {
            thread.start();
            thread.interrupt();
        });
        System.out.println("task started, do other work");
        try {
            int res = futureTask.get();
            System.out.println("res="+res);
        } catch (InterruptedException|ExecutionException e) {
            Logger.getLogger(FutureDemo.class.getName()).warning(e.toString());
        }

    }
}

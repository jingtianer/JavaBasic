package multithreading;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFeatureDemo {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    public CompletableFuture<List<Integer>> getList(int len) {
        return CompletableFuture.supplyAsync(()->{
            Random random = new Random();
            Integer[] array = new Integer[len];
            for(int i = 0; i < array.length; i++) {
                array[i] = random.nextInt(0, len);
                System.out.println(Thread.currentThread().getName() + ", getList");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            return new ArrayList<>(Arrays.asList(array));
        }, executorService);
    }
    public  List<List<Integer>> toMatrix(List<Integer> a, List<Integer> b) {
        List<List<Integer>> res = new ArrayList<>();
        loop:
        for (Integer x : a) {
            ArrayList<Integer> line = new ArrayList<>();
            for (Integer y : b) {
                line.add(x + y);
                System.out.println(Thread.currentThread().getName() + ", toMatrix");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break loop;
                }
            }
            res.add(line);
        }
        return res;
    }
    public <T> void printMatrix(List<List<T>> matrix) {
        System.out.println(matrix);
        executorService.shutdown();
    }
    void run() {
        var future1 = CompletableFuture
                .completedFuture(10)
                .thenComposeAsync(this::getList, executorService);
        var future2 = CompletableFuture
                .completedFuture(20)
                .thenComposeAsync(this::getList, executorService)
                .thenCombine(future1, this::toMatrix)
                .thenAccept(this::printMatrix);
        while (!executorService.isShutdown()) {
            System.out.println("do other work");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    public static void main(String[] args) {
        new CompletableFeatureDemo().run();
    }
}

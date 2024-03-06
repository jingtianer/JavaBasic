package multithreading;

import javax.naming.PartialResultException;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;
import java.util.logging.Logger;

class ArrayCounter<T> extends RecursiveTask<Integer> {
    T[] array;
    int start;
    int stop;
    Predicate<T> cond;
    int threshold;
    public ArrayCounter(T[] array, Predicate<T> cond, int threshold) {
        this(array, 0, array.length, cond, threshold);
    }
    public ArrayCounter(T[] array, int start, int stop, Predicate<T> cond, int threshold) {
        this.array = array;
        this.start = start;
        this.stop = stop;
        this.cond = cond;
        this.threshold = threshold;
    }
    int handle() {
        int cnt = 0;
        for (int i = start; i < stop; i++) {
            if (cond.test(array[i])) cnt++;
        }
        return cnt;
    }
    @Override
    protected Integer compute() {
//        System.out.println(Thread.currentThread().getName() + " compute [" + start + ", " + stop + ")");
        if(stop - start <= threshold) {
            return handle();
        } else {
            int mid = (stop - start) / 2 + start;
            ArrayCounter<T> subTask1 = new ArrayCounter<>(array, start, mid, cond, threshold);
            ArrayCounter<T> subTask2 = new ArrayCounter<>(array, mid, stop, cond, threshold);
            invokeAll(subTask1, subTask2);
            return subTask1.join() + subTask2.join();
        }
    }
}

public class ForkJoinDemo {
    private static final Logger logger = Logger.getLogger(ForkJoinDemo.class.getName());
    private static <T> void test(T[] array, Predicate<T> cond, int threshold) {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        var task = new ArrayCounter<>(array, cond, threshold);
        forkJoinPool.invoke(task);
        int taskCnt = task.join();
        long duration = System.currentTimeMillis() - start;
        int verifyCnt = 0;
        long start1 = System.currentTimeMillis();
        for(T t : array) {
            if(cond.test(t)) verifyCnt++;
        }
        long duration1 = System.currentTimeMillis() - start1;
        System.out.println("duration="+duration + ", duration1="+duration1 + ", promotion=" + ((duration1 - duration) / (double) duration));
        if(verifyCnt == taskCnt) {
            logger.fine("test ok!");
        } else {
            System.out.println("taskCnt="+taskCnt + ", verifyCnt="+verifyCnt);
            logger.warning("not ok");
        }
    }
    public static void main(String[] args) {
        Random random = new Random();
        Integer[] numbers = new Integer[100000000];
        Arrays.setAll(numbers, (i) -> random.nextInt(0, 10000));
//        System.out.println("numbers=" + Arrays.toString(numbers));
        test(numbers, (n)-> n%2 == 0, 100000000);
        test(numbers, (n)-> n%2 == 0, 10000000);
        test(numbers, (n)-> n%2 == 0, 1000000);
        test(numbers, (n)-> n%2 == 0, 100000);
        test(numbers, (n)-> n%2 == 0, 10000);
        test(numbers, (n)-> n%2 == 0, 1000);
        test(numbers, (n)-> n%2 == 0, 100);
        test(numbers, (n)-> n%2 == 0, 10);
        test(numbers, (n)-> n%2 == 0, 1);
    }
}

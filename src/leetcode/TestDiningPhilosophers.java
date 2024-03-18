package leetcode;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class DiningPhilosophers {
    Lock lock;
    boolean[] ready;
    Condition[] conditions;
    public DiningPhilosophers() {
        lock = new ReentrantLock();
        ready = new boolean[5];
        Arrays.fill(ready, true);
        conditions = new Condition[5];
        for (int i = 0; i < conditions.length; i++) {
            conditions[i] = lock.newCondition();
        }
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        lock.lock();
        while(!ready[philosopher] || !ready[(philosopher+1) % 5]) {
            conditions[philosopher].await();
        }
        ready[philosopher] = false;
        ready[(philosopher+1) % 5] = false;
        pickLeftFork.run();
        pickRightFork.run();
        lock.unlock();
        eat.run();
        lock.lock();
        putLeftFork.run();
        putRightFork.run();
        ready[philosopher] = true;
        ready[(philosopher+1) % 5] = true;
        conditions[(philosopher+1) % 5].signalAll();
        conditions[(philosopher-1 + 5) % 5].signalAll();
        lock.unlock();
    }
}
public class TestDiningPhilosophers {
    public static void main(String[] args) {
        DiningPhilosophers diningPhilosophers = new DiningPhilosophers();
        try (ExecutorService executorService = Executors.newFixedThreadPool(5)){
            for(int i = 0; i < 5; i++) {
                final int philosopherID = i;
                executorService.submit(()->{
                    try {
                        diningPhilosophers.wantsToEat(
                                philosopherID,
                                ()->System.out.printf("%s, pickLeftFork\n", Thread.currentThread().getName()),
                                ()->System.out.printf("%s, pickRightFork\n", Thread.currentThread().getName()),
                                ()->System.out.printf("%s, eat\n", Thread.currentThread().getName()),
                                ()->System.out.printf("%s, putLeftFork\n", Thread.currentThread().getName()),
                                ()->System.out.printf("%s, putRightFork\n", Thread.currentThread().getName())
                        );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}

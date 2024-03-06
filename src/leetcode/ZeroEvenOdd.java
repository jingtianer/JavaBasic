package leetcode;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

class ZeroEvenOdd {
    private int n;

    private int state;
    final private Lock lock;
    final private Condition condition;
    public ZeroEvenOdd(int n) {
        this.n = n;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        state = 0;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        for(int i = 0; i < n; i++) {
            synchronized (this) {
                while(state != 0 && state != 2) {
                    this.wait();
                }
                printNumber.accept(0);
                state = (state + 1) % 4;
                this.notifyAll();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        for(int i = 2; i <= n; i+=2) {
            synchronized (this) {
                while(state != 3) {
                    this.wait();
                }
                printNumber.accept(i);
                state = (state + 1) % 4;
                this.notifyAll();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        for(int i = 1; i <= n; i+=2) {
            synchronized (this) {
                while(state != 1) {
                    this.wait();
                }
                printNumber.accept(i);
                state = (state + 1) % 4;
                this.notifyAll();
            }
        }
    }
}

class Test {
    interface Tmp {
        void run(IntConsumer c) throws Exception;
    }
    static void runner(Tmp tmp, IntConsumer c) {
        try {
            tmp.run(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        int n = 5;
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(n);
        try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
            executorService.submit(()->runner(zeroEvenOdd::zero, System.out::println));
            executorService.submit(()->runner(zeroEvenOdd::even, System.out::println));
            executorService.submit(()->runner(zeroEvenOdd::odd, System.out::println));
        }
    }
}
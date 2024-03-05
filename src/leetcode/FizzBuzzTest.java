package leetcode;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;
import java.util.function.Predicate;

class FizzBuzz {
    private int n;
    final private Lock lock;
    final private Condition condition;
    private int curNum;
    public FizzBuzz(int n) {
        this.n = n;
        curNum = 1;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }
    //    private void runner(Predicate<Integer> test, IntConsumer consumer) throws InterruptedException {
//        while(curNum <= n) {
//            lock.lock();
//            while (!test.test(curNum) && curNum <= n) {
//                condition.await();
//            }
//            if(curNum <= n) {
//                consumer.accept(curNum);
//                curNum++;
//                condition.signalAll();
//            }
//            lock.unlock();
//        }
//    }
    private void runner(Predicate<Integer> test, IntConsumer consumer) throws InterruptedException {
        while(curNum <= n) {
            synchronized (this) {
                while (!test.test(curNum) && curNum <= n) {
                    this.wait();
                }
                if(curNum <= n) {
                    consumer.accept(curNum);
                    curNum++;
                    this.notifyAll();
                }
            }
        }
    }
    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        runner((n)->n % 3 == 0 && n % 5 != 0, (n)->printFizz.run());
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        runner((n)->n % 3 != 0 && n % 5 == 0, (n)->printBuzz.run());
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        runner((n)->n % 3 == 0 && n % 5 == 0, (n)->printFizzBuzz.run());
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        runner((n)->n % 3 != 0 && n % 5 != 0, printNumber);
    }
}
public class FizzBuzzTest {
    public static void main(String[] args) {
        if(args.length != 1) return;
        int n = Integer.parseInt(args[0]);
        FizzBuzz fizzBuzz = new FizzBuzz(n);
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            executor.submit(()->{
                try {
                    fizzBuzz.fizz(()-> System.out.printf("%s,fizz\n", Thread.currentThread().getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(()->{
                try {
                    fizzBuzz.buzz(()-> System.out.printf("%s,buzz\n", Thread.currentThread().getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(()->{
                try {
                    fizzBuzz.fizzbuzz(()-> System.out.printf("%s,fizzbuzz\n", Thread.currentThread().getName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            executor.submit(()->{
                try {
                    fizzBuzz.number((number)-> System.out.printf("%s,number,%d\n", Thread.currentThread().getName(), number));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
//            executor.shutdown();
        }

    }
}

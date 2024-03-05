package leetcode;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Foo {
    private int seq;
    public Foo() {
        seq = 0;
    }

    public void first(Runnable printFirst) throws InterruptedException {

        // printFirst.run() outputs "first". Do not change or remove this line.
        synchronized (this) {
            printFirst.run();
            seq++;
            notifyAll();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {

        // printSecond.run() outputs "second". Do not change or remove this line.
        synchronized (this) {
            while (seq != 1) {
                wait();
            }
            printSecond.run();
            seq++;
            notifyAll();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {

        // printThird.run() outputs "third". Do not change or remove this line.
        synchronized (this) {
            while (seq != 2) {
                wait();
            }
            printThird.run();
        }
    }
}
public class TestFoo {
    public static void main(String[] args) throws InterruptedException {
        Foo foo = new Foo();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(()-> {
            try {
                foo.first(()-> System.out.println("first"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.submit(()-> {
            try {
                foo.second(()-> System.out.println("second"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.submit(()-> {
            try {
                foo.third(()-> System.out.println("third"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }
}

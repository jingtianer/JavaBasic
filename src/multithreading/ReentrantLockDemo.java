package multithreading;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Worker extends Thread {
    private ReentrantLock lock;
    public Worker(ReentrantLock lock) {
        this.lock = lock;
    }
    @Override
    public void run() {
        lock.lock();
        try {
            System.out.println(this.getName() + ", run - lock");
            work();
        } finally {
            System.out.println(this.getName() + ", run - unlock");
            lock.unlock();
        }
    }

    private void work() {
        lock.lock();
        try {
            System.out.println(this.getName() + ", work - lock");
        } finally {
            System.out.println(this.getName() + ", work - unlock");
            lock.unlock();
        }
    }
}

class ReentrantTest extends Thread {
    final private static AtomicInteger workingCnt = new AtomicInteger();
    final Object lock;
    public ReentrantTest(Object lock) {
        this.lock = lock;
    }
    @Override
    public void run() {
        for(int i = 0; i < 100; i++) {
            doJob();
        }
    }
    public void doJob() {
        synchronized (lock) {
            int n = workingCnt.addAndGet(1);
            System.out.println(this.getName() + ", doJob=" + n);
            System.out.println(this.getName() + ", doneJob");
            workingCnt.addAndGet(-1);
        }
    }
}

public class ReentrantLockDemo {
    volatile String curThread = "None";
    public static void main(String[] args) {
        trySynchronized();
        tryReentrant();
        new ReentrantLockDemo().tryVolatile();
    }
    public void tryVolatile() {
        Thread observer = new Thread(() -> {
            while (!Thread.interrupted()) {
                System.out.println("cur Thread=" + curThread);
            }
        });
        observer.start();
        Thread[] threads = new Thread[100];
        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    curThread = getName();
                }
            };
            threads[i].start();
        }
        try {
            for(Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException ignored) {

        } finally {
            observer.interrupt();
        }
    }
    public static void trySynchronized() {
        Object lock = new Object();
        ReentrantTest[] workers = new ReentrantTest[1000];
        for(int i = 0; i < workers.length; i++) {
            workers[i] = new ReentrantTest(lock);
            workers[i].start();
        }

        for (ReentrantTest worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void tryReentrant() {
        ReentrantLock lock = new ReentrantLock();
        Worker[] workers = new Worker[10];
        for(int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(lock);
            workers[i].start();
        }
        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

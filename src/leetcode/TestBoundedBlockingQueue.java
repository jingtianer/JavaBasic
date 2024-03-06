package leetcode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static leetcode.Tools.printf;
import static leetcode.Tools.runCatching;

class BoundedBlockingQueue {
    Semaphore consumer, producer;
    Lock mutex;
    final private int capacity;
    private LinkedList<Integer> q = new LinkedList<>();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        consumer = new Semaphore(0);
        producer = new Semaphore(capacity);
        mutex = new ReentrantLock();
    }

    public void enqueue(int element) {
        try {
            producer.acquire();
            mutex.lock();
            q.add(element);
        } catch (Exception e) {
            producer.release();
            throw new RuntimeException(e);
        } finally {
            mutex.unlock();
            consumer.release();
        }
    }

    public int dequeue() {
        int ret;
        try {
            consumer.acquire();
            mutex.lock();
            ret = q.remove(0);
        } catch (Exception e) {
            consumer.release();
            throw new RuntimeException(e);
        } finally {
            mutex.unlock();
            producer.release();
        }
        return ret;
    }

    public int size() {
        return q.size();
    }
}
public class TestBoundedBlockingQueue {
    static final int MIN_THREAD_NUM = 2;
    static final int MAX_THREAD_NUM = 50;
    static final int MIN_CAPACITY = 1;
    static final int MAX_CAPACITY = 50;
    static final int MIN_OP_NUM = 20;
    static final int MAX_OP_NUM = 100;

    static
    Random random = new Random(System.currentTimeMillis());
    static int produceTotal; // 生产者生产总数
    static int consumerTotal; // 消费者总数
    static void getOps(int[] producerNum, int[] consumerNum, int maxRemainNum) {
        produceTotal = consumerTotal = 0;
        for(int i = 0; i < producerNum.length; i++){
            int opNum = random.nextInt(MIN_OP_NUM, MAX_OP_NUM + 1);
            producerNum[i] = opNum;
            produceTotal += opNum;
        }
        for(int i = consumerNum.length-1; produceTotal - maxRemainNum - consumerTotal > 0; i--) {
            int opNum = random.nextInt((produceTotal - maxRemainNum - consumerTotal) / (i + 1), (produceTotal - maxRemainNum - consumerTotal) + 1);
            consumerNum[i] = opNum;
            consumerTotal += opNum;
        }
    }
    public static void main(String[] args) {
        int threadNum = random.nextInt(MIN_THREAD_NUM, MAX_THREAD_NUM + 1);
        int capacity = random.nextInt(MIN_CAPACITY, MAX_CAPACITY + 1);
        int maxRemainNum = random.nextInt(0, capacity + 1);
        int[] producerNum = new int[random.nextInt(1, threadNum)];
        int[] consumerNum = new int[threadNum - producerNum.length];
        getOps(producerNum, consumerNum, maxRemainNum);
        System.out.printf("producerNum=%s\nconsumerNum=%s\nthreadNum=%d\ncapacity=%d\nproducerTotal=%d\nconsumerTotal=%d\nsize=%d\nmaxRemainNum=%d\n",
                Arrays.toString(producerNum), Arrays.toString(consumerNum), threadNum, capacity, produceTotal, consumerTotal, produceTotal - consumerTotal, maxRemainNum);
        BoundedBlockingQueue boundedBlockingQueue = new BoundedBlockingQueue(capacity);
        try(ExecutorService executorService = Executors.newFixedThreadPool(threadNum)) {
            for(int i = 0; i < producerNum.length; i++) {
                final int id = i;
                executorService.submit(runCatching(() -> {
                    for(int j = 0; j < producerNum[id]; j++) {
                        System.out.printf("%s, enqueue\n", Thread.currentThread().getName());
                        boundedBlockingQueue.enqueue(id);
                    }
                }));
            }
            for(int i = 0; i < consumerNum.length; i++) {
                final int id = i;
                executorService.submit(runCatching(() -> {
                    for(int j = 0; j < consumerNum[id]; j++) {
                        int front = boundedBlockingQueue.dequeue();
                        System.out.printf("%s, dequeue, front = %d\n", Thread.currentThread().getName(), front);
                    }
                }));
            }
        }
        if(produceTotal - consumerTotal == boundedBlockingQueue.size()) {
            System.out.printf("ok!, size = %d\n", produceTotal - consumerTotal);
        } else {
            throw new RuntimeException(printf("fail, you are foolish, correct size = %d, q.size = %d\n", produceTotal - consumerTotal, boundedBlockingQueue.size()));
        }
    }
}
package leetcode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    public void enqueue(int element) throws InterruptedException {
        producer.acquire();
        mutex.lock();
        q.add(element);
        mutex.unlock();
        consumer.release();
    }

    public int dequeue() throws InterruptedException {
        consumer.acquire();
        mutex.lock();
        int ret = q.remove(0);
        mutex.unlock();
        producer.release();
        return ret;
    }

    public int size() {
        return q.size();
    }
}
public class TestBoundedBlockingQueue {
    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        int threadNum = random.nextInt(2,10);
        int capacity = random.nextInt(1,100);
        List<Map.Entry<Integer, Boolean>> t_ops = null;
        Map.Entry<Integer, Boolean> ok = Map.entry(0, false);
        boolean hasFalse = false;
        while(ok.getKey() == 0 || !ok.getValue() || !hasFalse) { //保证不是全true，不是全false
            t_ops = new ArrayList<>(threadNum);
            for(int i = 0; i < threadNum; i++){
                boolean isProducer = random.nextBoolean();
                t_ops.add(Map.entry(random.nextInt(5, 30), isProducer));
                hasFalse = hasFalse || !isProducer;
            }
            ok = t_ops.stream()
                    .reduce((left, right) -> {
                        int sum = (left.getValue() ? left.getKey() : 0) + (right.getValue() ? right.getKey() : 0);
                        return Map.entry(sum, left.getValue() || right.getValue());
                    })
                    .orElse(Map.entry(0, false));
        }
        int produceTotal = ok.getKey(); // 生产者生产总数
        final List<Map.Entry<Integer, Boolean>> ops = t_ops;
        System.out.println(produceTotal);
        for(int i = 0; i < threadNum; i++) {
            if(ops.get(i).getValue()) continue;
            if(produceTotal == 0) {
                ops.set(i, Map.entry(0, false));
            } else {
                int nums = random.nextInt(1, produceTotal+1); // 消费者消费数目随机，且不全为0
                produceTotal -= nums;
                ops.set(i, Map.entry(nums, false));
            }
        }
        if(produceTotal > capacity) { // 生产数量-消费数量不能大于容量
            for(int i = 0; i < threadNum; i++) {
                if(!ops.get(i).getValue()) {
                    ops.set(i, Map.entry(ops.get(i).getKey() + produceTotal - random.nextInt(0, capacity), false));
                    break;
                }
            }
        }
        System.out.println(ops);
        BoundedBlockingQueue boundedBlockingQueue = new BoundedBlockingQueue(capacity);
        int result = 0;
        try(ExecutorService executorService = Executors.newFixedThreadPool(threadNum)) {
            for(int i = 0; i < threadNum; i++) {
                final int id = i;
                if(ops.get(i).getValue()) {
                    executorService.submit(()->{
                        for(int j = 0; j < ops.get(id).getKey(); j++) {
                            try {
                                boundedBlockingQueue.enqueue(id);
//                                System.out.println(Thread.currentThread().getName() + ", enqueue!");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    result += ops.get(id).getKey();
                } else {
                    executorService.submit(()->{
                        for(int j = 0; j < ops.get(id).getKey(); j++) {
                            try {
                                boundedBlockingQueue.dequeue();
//                                System.out.println(Thread.currentThread().getName() + ", dequeue!");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    result -= ops.get(id).getKey();
                }
            }
        }
        if(result == boundedBlockingQueue.size()) {
            System.out.println("ok!, size = " + result);
        } else {
            System.out.printf("fail, you are foolish, correct size = %d, q.size = %d\n", result, boundedBlockingQueue.size());
        }
    }
}

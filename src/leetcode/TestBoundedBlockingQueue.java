package leetcode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class BoundedBlockingQueue {
    Semaphore consumer, producer;
    final private int capacity;
    private LinkedList<Integer> q = new LinkedList<Integer>();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        consumer = new Semaphore(0);
        producer = new Semaphore(capacity);
    }

    public void enqueue(int element) throws InterruptedException {
        producer.acquire();
        q.add(element);
        consumer.release();
    }

    public int dequeue() throws InterruptedException {
        consumer.acquire();
        int ret = q.remove(0);
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
        List<Map.Entry<Integer, Boolean>> ops = new ArrayList<>(threadNum);
        for(int i = 0; i < threadNum; i++)
            ops.add(Map.entry(random.nextInt(5, 30), random.nextBoolean()));
        System.out.println(ops);
        int produceTotal = 0;
        while(produceTotal == 0) {
            produceTotal = ops.stream()
                    .reduce((left, right) -> {
                        int sum = (left.getValue() ? left.getKey() : 0) + (right.getValue() ? right.getKey() : 0);
                        return Map.entry(sum, true);
                    })
                    .get()
                    .getKey();
        }
        System.out.println(produceTotal);
        for(int i = 0; i < threadNum; i++) {
            if(ops.get(i).getValue()) continue;
            if(produceTotal == 0) {
                ops.set(i, Map.entry(0, false));
            } else {
                int nums = random.nextInt(0, produceTotal);
                produceTotal -= nums;
                ops.set(i, Map.entry(nums, false));
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
                                System.out.println(Thread.currentThread().getName() + ", enqueue!");
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
                                System.out.println(Thread.currentThread().getName() + ", dequeue!");
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
            System.out.println("ok!");
        } else {
            System.out.println("fail, you are foolish");
        }
    }
}

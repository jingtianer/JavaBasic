package leetcode;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TrafficLight {
    private int road = 0;
    private final Semaphore lockNS;
    private final Semaphore lockSW;
    private static final int NS = 1;
    private static final int SW = 2;
    private int carNumber = 0;
    public TrafficLight() {
        lockNS = new Semaphore(1);
        lockSW = new Semaphore(1);
    }

    public void carArrived(int carId, // ID of the car
                                        int roadId, // ID of the road the car travels on. Can be 1 (road A) or 2 (road B)
                                        int direction, // Direction of the car
                                        Runnable turnGreen, // Use turnGreen.run() to turn light to green on current road
                                        Runnable crossCar // Use crossCar.run() to make car cross the intersection
    ) {
        Semaphore lockOur, lockOther;
        if(roadId == NS) {
            lockOur = lockNS;
            lockOther = lockSW;
        } else {
            lockOur = lockSW;
            lockOther = lockNS;
        }
        try {
            lockOur.acquire();
            if(roadId != road) {
                lockOther.acquire();
                road = roadId;
                turnGreen.run();
            }
            carNumber++;
            lockOur.release();

            crossCar.run();

            lockOur.acquire();
            carNumber--;
            if(carNumber == 0) {
                lockOther.release();
            }
            lockOur.release();
        } catch (InterruptedException e) {

        }
    }
}

public class TestTrafficLight {
    public static void main(String[] args) {
        final TrafficLight trafficLight = new TrafficLight();
        Random random = new Random(System.currentTimeMillis());
        int carNumber = random.nextInt(100, 200);
        try(ExecutorService executor = Executors.newCachedThreadPool()) {
            for(int i = 0; i < carNumber; i++) {
                final int carId = i;
                final int roadId = random.nextInt(1,3);
                final int direction = random.nextInt(1,3);
                executor.submit(()->trafficLight.carArrived(
                        carId,
                        roadId,
                        direction,
                        ()-> System.out.printf("car:%d, onRoad:%d, direction:%d, turn traffic light green\n", carId, roadId, direction),
                        ()-> System.out.printf("car:%d, onRoad:%d, direction:%d, running\n", carId, roadId, direction)
                ));
            }
        }
    }
}

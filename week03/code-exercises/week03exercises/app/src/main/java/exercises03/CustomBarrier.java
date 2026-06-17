package exercises03;

public class CustomBarrier {

    private final CustomSemaphore semaphore;
    private volatile int threadsThatMustReachBarrier;
    private int currentThreads;

    public CustomBarrier(int threadsThatMustReachBarrier) {
        this.threadsThatMustReachBarrier = threadsThatMustReachBarrier;
        this.semaphore = new CustomSemaphore(0);
    }

    public void await() throws InterruptedException {
        synchronized(this) {
            currentThreads++;
            if (currentThreads == threadsThatMustReachBarrier) {
                //1. Goes from 0 -> 1.
                semaphore.release();
            }
        }
        //2. Goes from 1 -> 0.
        semaphore.acquire();
        //3. Unlock. Goes from 0 -> 1. Next thread starts at point 2.
        semaphore.release();
    }


    public static void main(String[] args) {
        int totalThreads = 5;  // Number of threads that must reach the barrier
        CustomBarrier barrier = new CustomBarrier(totalThreads);

        // Create and start multiple threads
        for (int i = 0; i < totalThreads; i++) {
            int threadId = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadId + " is doing some work.");
                    //Thread.sleep((long) (Math.random() * 1000));  // Simulate random work time

                    System.out.println("Thread " + threadId + " is waiting at the barrier.");
                    barrier.await();  // Wait for other threads to reach the barrier

                    System.out.println("Thread " + threadId + " passed the barrier.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

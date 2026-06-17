package exercises09.ExamShowcase;

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
}

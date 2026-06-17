package exercises06;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentTestRunner {
    private final int threadCount;
    private final ExecutorService executor;

    public ConcurrentTestRunner(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newWorkStealingPool();
    }

    public void runConcurrently(Runnable operation) throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await();
                    operation.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        //It only starts executing right HERE
        startLatch.countDown();
        //Makes sure the main thread is still running, till all threads are done
        //otherwise it would instantly stop right here.
        endLatch.await();
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
}
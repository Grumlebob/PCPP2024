package exercises04;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemaphoreTest {

    private final int threadCount = 10;
    private final int capacity = 2;
    private SemaphoreImp semaphore;
    private ConcurrentTestRunner testRunner;

    @BeforeEach
    public void initialize() {
        semaphore = new SemaphoreImp(capacity);
        testRunner = new ConcurrentTestRunner(threadCount);
    }

    @AfterEach
    public void cleanup() throws InterruptedException {
        testRunner.shutdown();
    }

    @RepeatedTest(5000)
    public void testSemaphore() throws InterruptedException {
        //Var to keep track if we exceed the capacity
        AtomicInteger maxConcurrentAccesses = new AtomicInteger(0);
        //Var to keep track of the actual number of threads accessing the semaphore
        AtomicInteger currentAccesses = new AtomicInteger(0);

        // KEY POINT - We can have state = -1, which shouldn't be possible
        // Let's say capacity = 2.
        // We start with release, which sets state to -1
        // Now we can allow 3 threads to acquire(), thus surpassing our limit of 2
        semaphore.release();

        testRunner.runConcurrently(() -> {
            try {
                semaphore.acquire();
                int accesses = currentAccesses.incrementAndGet();
                maxConcurrentAccesses.updateAndGet(max -> Math.max(max, accesses));
                // Simulate some work in the critical section
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                currentAccesses.decrementAndGet();
                semaphore.release();
            }
        });

        assertTrue(maxConcurrentAccesses.get() <= capacity,
                "The maximum number of concurrent accesses exceeded the semaphore's capacity.");
    }
}
package exercises04;

import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderWriterImplSemaphoreTest {

    @RepeatedTest(5000)
    public void testMaxFiveReaders() throws InterruptedException {
        ReaderWriterImplSemaphore rwLock = new ReaderWriterImplSemaphore();
        int numThreads = 100;
        ConcurrentTestRunner runner = new ConcurrentTestRunner(numThreads);
        AtomicBoolean testPassed = new AtomicBoolean(true);
        AtomicInteger maxReadersObserved = new AtomicInteger(0);

        runner.runConcurrently(() -> {
            if (Math.random() < 0.5) {
                rwLock.readCriticalZone();
                int currentReaders = rwLock.numberOfReadersActive;
                maxReadersObserved.updateAndGet(max -> Math.max(max, currentReaders));
                if (currentReaders > 5) {
                    testPassed.set(false);
                }
            } else {
                rwLock.writeCriticalZone();
            }
        });

        runner.shutdown();

        assertTrue(testPassed.get(), "More than 5 readers accessed the resource simultaneously");
        assertTrue(maxReadersObserved.get() <= 5, "Maximum number of concurrent readers exceeded 5");
        System.out.println("Maximum number of concurrent readers observed: " + maxReadersObserved.get());
    }
}
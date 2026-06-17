package exercises04;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrentSetTest {

    private ConcurrentIntegerSet set;
    private ConcurrentTestRunner testRunner;

    private final int threadCount = 10;

    @BeforeEach
    public void initialize() {
        set = new ConcurrentIntegerSetSync();
        //set = new ConcurrentIntegerSetBuggy();
        //set = new ConcurrentIntegerSetLibrary();
        testRunner = new ConcurrentTestRunner(threadCount);
    }

    @AfterEach
    public void cleanup() throws InterruptedException {
        testRunner.shutdown();
    }

    @RepeatedTest(5000)
    public void testConcurrentAdd() throws InterruptedException {
        testRunner.runConcurrently(() -> set.add(1));
        assertEquals(1, set.size(), "The size of the set should be 1 if it is thread-safe");
    }

    @RepeatedTest(5000)
    public void testConcurrentRemove() throws InterruptedException {
        set.add(1);
        testRunner.runConcurrently(() -> set.remove(1));
        assertEquals(0, set.size(), "The size of the set should be 0 if it is thread-safe");
    }
}
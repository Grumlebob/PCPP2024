package exercises05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocks {

    private ReadWriteCASLock lock;
    private ConcurrentTestRunner testRunner;

    @BeforeEach
    public void setup() {
        lock = new ReadWriteCASLock();
        testRunner = new ConcurrentTestRunner(100);  // 10 threads for parallel tests
    }

    // 5. Sequential Tests
    @Test
    @DisplayName("It is not possible to take a read lock while holding a write lock.")
    public void testCannotAcquireReadLockWhileHoldingWriteLock() {
        assertTrue(lock.writerTryLock(), "Writer should acquire the lock");
        assertFalse(lock.readerTryLock(), "Reader should not acquire the lock while writer holds it");
    }

    @Test
    @DisplayName("It is not possible to take a write lock while holding a read lock.")
    public void testCannotAcquireWriteLockWhileHoldingReadLock() {
        assertTrue(lock.readerTryLock(), "Reader should acquire the lock");
        assertFalse(lock.writerTryLock(), "Writer should not acquire the lock while reader holds it");
    }

    @Test
    @DisplayName("It is not possible to unlock a lock that you do not hold (both for read and write unlock).")
    public void testCannotUnlockWithoutHoldingLock() {
        assertThrows(IllegalMonitorStateException.class, lock::readerUnlock, "Reader should not unlock without holding the lock");
        assertThrows(IllegalMonitorStateException.class, lock::writerUnlock, "Writer should not unlock without holding the lock");
    }

    // 6. Parallel Test for Multiple Writers
    @Test
    @DisplayName("checks that two writers cannot acquire the lock at\n" +
            "the same time")
    public void testParallelWriters() throws InterruptedException {
        final AtomicBoolean multipleWriters = new AtomicBoolean(false);
        final AtomicBoolean writerLockViolation = new AtomicBoolean(false);

        // Create a ConcurrentTestRunner with enough threads
        testRunner = new ConcurrentTestRunner(10);

        // Run multiple threads attempting to acquire the writer lock
        testRunner.runConcurrently(() -> {

            //Only 1 writer should be able to get in.
            if (lock.writerTryLock()) {
                try {
                    //Lets assume 1 writer.
                    //First writer sets multipleWriters to true
                    //But doesn't enter if block
                    //Now lets assume 2nd writer comes in
                    //compareAndSet expects value to be false, but it is true
                    //So the 2nd writer enters if statement and sets writerLockViolation to true
                    if (!multipleWriters.compareAndSet(false, true)) {
                        writerLockViolation.set(true);  // Violation if multiple writers are holding the lock
                    }
                    Thread.sleep(50);  // Simulate holding the lock briefly
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    //first writer exits, and sets multipleWriters to false
                    //we must first clear this state, before releasing the lock
                    multipleWriters.set(false);
                    //and releases the lock
                    lock.writerUnlock();
                }
            }
        });


        assertFalse(writerLockViolation.get(), "Multiple writers should not be able to hold the lock at the same time");

        // Shut down the runner
        testRunner.shutdown();
    }
}

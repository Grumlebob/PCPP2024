package exercises09;

import exercises09.ExamShowcase.CustomReaderWriterLock;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class CustomReaderWriterLockTest {

    //----Specifications of readLock()----
    // Case 1: Space for reader
    // Pre-condition: numberOfReadersActive < 5 && !isWriterInQueue
    // Post-condition: numberOfReadersActive++, semaphore is acquired
    @Test
    public void testReadLock() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        rwLock.readLock();
        assertTrue(rwLock.getNumberOfReadersActive() == 1);
    }

    // Case 2: No space for reader
    // Pre-condition: numberOfReadersActive = 5
    // Post-condition: numberOfReadersActive is the same, the thread is blocked
    @Test
    public void testReadLockNoSpace() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        for (int i = 0; i < 5; i++) {
            rwLock.readLock();
        }
        //Try to let in a 6th reader
        Thread sixthReader = new Thread(rwLock::readLock);
        sixthReader.start();
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        //Readers should still be 5
        assertEquals(5, rwLock.getNumberOfReadersActive());
        //6th reader should be waiting.
        assertEquals(Thread.State.WAITING, sixthReader.getState());
    }

    // Case 3: Writer in queue
    // Pre-condition: isWriterInQueue
    // Post-condition: numberOfReadersActive is the same, the thread is blocked
    @Test
    public void testReadLockWhileWriterInQueue() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        rwLock.writeLock();
        Thread newReader = new Thread(rwLock::readLock);
        newReader.start();
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        //No readers should be able to enter
        assertEquals(0, rwLock.getNumberOfReadersActive());
        //New reader should be waiting.
        assertEquals(Thread.State.WAITING, newReader.getState());
    }

    //----Specification of readUnlock()----
    // Pre-condition: Reader holds lock. numberOfReadersActive = k
    // Post-condition: numberOfReadersActive = k-1, semaphore is released
    @Test
    public void testReadUnlock() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        rwLock.readLock();
        int readersActiveAfterLock = rwLock.getNumberOfReadersActive();
        assertEquals(rwLock.getNumberOfReadersActive(), 1);
        rwLock.readUnlock();
        assertEquals(rwLock.getNumberOfReadersActive(), readersActiveAfterLock - 1);
    }

    //----Specification of writeLock()----
    // Case 1: Space for writer
    // Pre-condition: !isWriterInQueue
    // Post-condition: isWriterInQueue == true
    @Test
    public void testWriteLock() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        rwLock.writeLock();
        assertTrue(rwLock.isWriterInQueue());
    }

    // Case 2: No space for writer
    // Pre-condition: isWriterInQueue
    // Post-condition: isWriterInQueue == true, the thread is blocked
    @Test
    public void testWriteLockNoSpace() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        //First writer locks
        rwLock.writeLock();
        //Second writer tries to lock
        Thread secondWriter = new Thread(rwLock::writeLock);
        secondWriter.start();
        try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
        //A writer should be in queue
        assertTrue(rwLock.isWriterInQueue());
        //Second writer should be waiting.
        assertEquals(Thread.State.WAITING, secondWriter.getState());
    }

    //----Specification of writeUnlock()----
    // Pre-condition: isWriterInQueue == true
    // Post-condition: isWriterInQueue == false. Condition is signalled
    @Test
    public void testWriteUnlock() {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        rwLock.writeLock();
        rwLock.writeUnlock();
        assertFalse(rwLock.isWriterInQueue());
    }

    //-------Uncovered cases--------:
    // * One may be able to unlock before locking, even without holding the lock

    //----Combined test, with latch for contention------
    //RepeatedTest() - stopped working the day before exam in this project :(
    @Test
    public void testMaxFiveReaders() throws InterruptedException {
        CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
        int numThreads = 100;
        ConcurrentTestRunner runner = new ConcurrentTestRunner(numThreads);
        AtomicBoolean tooManyReaders = new AtomicBoolean(false);
        AtomicInteger maxReadersObserved = new AtomicInteger(0);
        final AtomicBoolean aWriterAlreadyHoldsLock = new AtomicBoolean(false);
        final AtomicBoolean hasTwoWritersHeldLockConcurrently = new AtomicBoolean(false);

        runner.runConcurrently(() -> {
            //50 % chance of being a reader.
            if (Math.random() < 0.5) {
                rwLock.readLock();
                try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                int currentReaders = rwLock.getNumberOfReadersActive();
                //Ensure max 5 readers
                maxReadersObserved.updateAndGet(max -> Math.max(max, currentReaders));
                if (currentReaders > 5) {
                    tooManyReaders.set(true);
                }
                rwLock.readUnlock();
            }
            //50 % chance of being a writer.
            else {
                rwLock.writeLock();
                //Ensures only 1 writer at a time
                if (aWriterAlreadyHoldsLock.get()) {
                    hasTwoWritersHeldLockConcurrently.set(true);
                }
                //We are holding the lock.
                aWriterAlreadyHoldsLock.set(true);
                try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
                finally {
                    //Release the lock
                    aWriterAlreadyHoldsLock.set(false);
                    rwLock.writeUnlock();
                }
            }
        });

        runner.shutdown();

        assertFalse(tooManyReaders.get(), "More than 5 readers accessed the resource simultaneously");
        assertTrue(maxReadersObserved.get() <= 5, "Maximum number of concurrent readers exceeded 5");
        assertFalse(hasTwoWritersHeldLockConcurrently.get(), "Multiple writers were able to access the resource simultaneously");
        System.out.println("Maximum number of concurrent readers observed: " + maxReadersObserved.get());
    }
}
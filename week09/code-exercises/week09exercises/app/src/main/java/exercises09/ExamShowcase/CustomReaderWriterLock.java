package exercises09.ExamShowcase;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomReaderWriterLock {
    //Info: Biased towards writers. And readers can starve.

    //State variables
    private int numberOfReadersActive = 0;

    //Act as our "lock" for the writer
    private volatile boolean isWriterInQueue = false;
    //Act as our "lock" for the readers
    private CustomSemaphore semaphore = new CustomSemaphore(5);

    //Used internally for this RW-Lock
    private Lock lock = new ReentrantLock(true); //fifo
    private Condition condition = lock.newCondition();

    public void readLock() {
        try {
            //allow 5 readers
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Used internally for this lock.
        lock.lock();
        try {
            //Not starvation free. Block if writer is in queue
            while (isWriterInQueue) {
                condition.await();
            }
            numberOfReadersActive++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void readUnlock() {
        lock.lock();
        try {
            numberOfReadersActive--;
            if (numberOfReadersActive == 0) {
                condition.signalAll();
            }
        } finally {
            semaphore.release();
            lock.unlock();
        }
    }


    public void writeLock() {
        lock.lock();
        try {
            while (isWriterInQueue) {
                condition.await(); //release lock and wait
            }
            //Declare our self as the writer.
            isWriterInQueue = true;

            //Above creates a small window, where a reader might have snuck in.
            //This ensures that no readers are active.
            while (numberOfReadersActive > 0) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void writeUnlock() {
        lock.lock();
        try {
            isWriterInQueue = false;
            condition.signalAll(); //notify waiting readers/writers, to check condition again
        } finally {
            lock.unlock();
        }
    }

    public int getNumberOfReadersActive() {
        return numberOfReadersActive;
    }

    public boolean isWriterInQueue() {
        return isWriterInQueue;
    }


}

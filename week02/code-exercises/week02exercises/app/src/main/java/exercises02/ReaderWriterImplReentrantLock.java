package exercises02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterImplReentrantLock {

    //Shared resource
    private int sharedResource = 0;

    //State variables
    public int numberOfReadersActive = 0;
    private volatile boolean isWriterInQueue = false;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void readCriticalZone() {
        readLock();
        System.out.println("Reader number " + numberOfReadersActive + " is reading " + sharedResource);
        readUnlock();
    }

    private void readLock() {
        lock.lock();
        try {
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

    private void readUnlock() {
        lock.lock();
        try {
            numberOfReadersActive--;
            if (numberOfReadersActive == 0) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void writeCriticalZone() {
        writeLock();
        sharedResource++;
        System.out.println("Writer is writing " + sharedResource);
        writeUnlock();
    }



    private void writeLock() {
        lock.lock();
        try {
            while (isWriterInQueue) {
                condition.await();
            }
            isWriterInQueue = true;
            while (numberOfReadersActive > 0) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void writeUnlock() {
        lock.lock();
        try {
            isWriterInQueue = false;
            condition.signalAll(); //notify waiting readers, to check condition again
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

        //Driver of the program
        ReaderWriterImplReentrantLock readerWriterImplReentrantLock = new ReaderWriterImplReentrantLock();

        for (int i = 0; i < 10; i++) {
            // start a writer
            new Thread(() -> {
                readerWriterImplReentrantLock.writeCriticalZone();
            }).start();
            // start a reader
            new Thread(() -> {
                readerWriterImplReentrantLock.readCriticalZone();
            }).start();
            // start a reader
            new Thread(() -> {
                readerWriterImplReentrantLock.readCriticalZone();
            }).start();
            // start a writer
            new Thread(() -> {
                readerWriterImplReentrantLock.writeCriticalZone();
            }).start();
            // start a reader
            new Thread(() -> {
                readerWriterImplReentrantLock.readCriticalZone();
            }).start();
        }


    }
}

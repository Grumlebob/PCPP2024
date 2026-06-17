package exercises02;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterImplIntrinsicLock {

    //Shared resource
    private int sharedResource = 0;

    //State variables
    public int numberOfReadersActive = 0;
    private volatile boolean isWriterInQueue = false;

    public void readCriticalZone() {
        readLock();
        System.out.println("Reader number " + numberOfReadersActive + " is reading " + sharedResource);
        readUnlock();
    }

    private synchronized void readLock() {
        try {
            while (isWriterInQueue) {
                wait();
            }
            numberOfReadersActive++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void readUnlock() {
        try {
            numberOfReadersActive--;
            if (numberOfReadersActive == 0) {
                notifyAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCriticalZone() {
        writeLock();
        sharedResource++;
        System.out.println("Writer is writing " + sharedResource);
        writeUnlock();
    }


    private synchronized void writeLock() {
        try {
            while (isWriterInQueue) {
                wait();
            }
            isWriterInQueue = true;
            while (numberOfReadersActive > 0) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void writeUnlock() {
        try {
            isWriterInQueue = false;
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //Driver of the program
        ReaderWriterImplIntrinsicLock readerWriterImplReentrantLock = new ReaderWriterImplIntrinsicLock();

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

// For week 3
// raup@itu.dk * 01/09/2021
// raup@itu.dk * 15/09/2022

package lecture03;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadersWritersSemaphore {

    public ReadersWritersSemaphore() {

        FairReadWriteMonitor m  = new FairReadWriteMonitor();
        Semaphore semReaders    = new Semaphore(5,true);
        Semaphore semWriters    = new Semaphore(5,true);
        AtomicInteger noReaders = new AtomicInteger(0);
        AtomicInteger noWriters = new AtomicInteger(0);

        final int numReadersWriters = 20;

        for (int i = 0; i < numReadersWriters; i++) {

            // start a reader
            new Thread(() -> {
                    m.readLock();
                    try{semReaders.acquire();}catch(InterruptedException e){e.printStackTrace();System.exit(-1);}
                    // Note that it always prints less than 6 readers (do not mind the printing order)
                    System.out.println("There are " + noReaders.incrementAndGet() + " threads reading");
                    // read
                    semReaders.release();
                    noReaders.decrementAndGet();
                    m.readUnlock();
            }).start();

            // start a writer
            new Thread(() -> {
                    m.writeLock();
                    try{semWriters.acquire();}catch(InterruptedException e){e.printStackTrace();System.exit(-1);}
                    System.out.println("There are " + noWriters.incrementAndGet() + " threads writing");
                    // write
                    semWriters.release();
                    noWriters.decrementAndGet();
                    m.writeUnlock();
            }).start();

        }
    }

    public static void main(String[] args) {
        new ReadersWritersSemaphore();
    }

}

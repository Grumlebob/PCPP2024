// jst@itu.dk * 04/10/2023
package exercises09.ExamShowcase;

import exercises09.Histogram;
import exercises09.StarvedRWLock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class ThreadSafeHistogram implements Histogram {
    public static int NumberOfHistograms = 42; //static
    private Object object = new Object(); //default
    private volatile String label; //flush
    private AtomicInteger bins = new AtomicInteger(42); //internal volatile
    public final String FontColor = "red"; //immutable
    private int[] counts; //synchronized in constructor
    private int total = 0; //default

    public ThreadSafeHistogram(int span) {
        synchronized (this) {
            this.counts = new int[span];
        }
    }

    public synchronized void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    //No references escapes
    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }

    public int getTotal() {
        return total;
    }


    //------Unsafe zone for demonstration-------

    //Critical zone
    public void incrementUnsynchronized(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    //Starvation
    // r1(1) -> r1(2) -> r2(3) -> r2(4) -> r1(1) -> r1(2) -> ... (w(5) never happens)
    public void readersOverruleWriter() throws InterruptedException {
        var rwLock = new StarvedRWLock();
        var r1 = new Thread(() -> {
            while (true) {
                rwLock.readLockWithFullReaderPriority(); // (1)
                System.out.println("Reading...");
                rwLock.readUnlock(); // (2)
            }
        });
        var r2 = new Thread(() -> {
            while (true) {
                rwLock.readLockWithFullReaderPriority(); // (3)
                System.out.println("Reading...");
                rwLock.readUnlock(); // (4)
            }
        });
        var w = new Thread(() -> {
            rwLock.writeLock(); // (5)
            //...
        });
        r1.start(); r2.start(); w.start();
        r1.join(); r2.join(); w.join();
    }

    //Deadlock
    // T1(1) -> T2(3)
    // T1(3) -> T2(1)
    public void printFirstTwoBinsTwice() {
        var lockBin0 = new Object();
        var lockBin1 = new Object();
        new Thread(() -> { // T1
            synchronized (lockBin0) { // (1)
                System.out.println("Bin 0: " + counts[0]);
                synchronized (lockBin1) { // (2)
                    System.out.println("Bin 1: " + counts[1]);
                }
            }
        }).start();
        new Thread(() -> { // T2
            synchronized (lockBin1) { // (3)
                System.out.println("Bin 1: " + counts[1]);
                synchronized (lockBin0) { // (4)
                    System.out.println("Bin 0:" + counts[0]);
                }
            }
        }).start();
    }

    // Livelock
    // t1(1) -> t2(7) -> t1(2) -> t2(8) -> t1(3) -> t2(9) -> t1(1) -> t2(7) -> ...
    // Left fork locked by A, right fork locked by B. Left release fork. Right release fork.
    public void noYouEatFirst() {
        var leftFork = new ReentrantLock();
        var rightFork = new ReentrantLock();
        var t1 = new Thread(() -> {
            while (true) {
                leftFork.lock(); // (1)
                if (rightFork.isLocked()) { // (2)
                    leftFork.unlock(); // (3) give the other thread a chance to eat
                }
                else {
                    rightFork.lock(); // (4)
                    // eat
                    leftFork.unlock(); // (5)
                    rightFork.unlock(); // (6)
                    break;
                }
            }
        });
        var t2 = new Thread(() -> {
            while (true) {
                rightFork.lock(); // (7)
                if (leftFork.isLocked()) { // (8)
                    rightFork.unlock(); // (9)
                }
                else {
                    leftFork.lock(); // (10)
                    // eat
                    leftFork.unlock(); // (11)
                    rightFork.unlock(); // (12)
                    break;
                }
            }
        });
    }

    //----------Thread-safety------------------

    //Data race
    // T1() || T2()
    public void resetTotalTwice() {
        new Thread(() -> total = 0).start(); // T1
        new Thread(() -> total = 0).start(); // T2
    }

    //Race condition
    // Possible: T1(1) -> T2(1) -> T3(1)...
    // Or: T2(1) -> T1(1) -> T3(1)...
    public void printAllBins() {
        for (int i = 0; i < counts.length; i++) {
            var index = i;
            new Thread(() -> // T1, T2, T3, ...
                    System.out.println("Bin " + index + " has count " + counts[index])) // (1)
                    .start();
        }
    }

    //Escaping
    //X1.getCountsArray() -> X2.getCountsArray()
    //X1.modifyCountsArrayConcurrently() || X2.modifyCountsArrayConcurrently()
    public synchronized int[] getCountsArray() {
        return counts;
    }

    //Jump to top for safe publication demo.


    //Visibility violation
    // t1(1) || t2(2)
    public void lackOfVisibility() throws InterruptedException {
        var t1 = new Thread(() -> {
            try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
            total = 42; // (1)
        });
        var t2 = new Thread(() -> {
            while (total != 42) { // (2)
                //wait
            }
            System.out.println("Total is 42"); // (3)
        });
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

    // Reordering
    // t1(1) -> t1(2) -> t2(3) -> t2(4): a = 1, b = 2
    // t1(1) -> t2(3) -> t1(2) -> t2(4): a = 1, b = 0
    // t2(4) -> t1(1) -> t1(2) -> t2(3): a = 0, b = 2 (reordered)
    public void reordering() throws InterruptedException {
        counts[0] = 0;
        counts[1] = 0;
        var t1 = new Thread(() -> {
            counts[0] = 1; // (1)
            counts[1] = 2; // (2)
        });
        var t2 = new Thread(() -> {
            int b = counts[1]; // (3)
            int a = counts[0]; // (4)
        });
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

    //Jump to drawio for sequential consistency demo.

}

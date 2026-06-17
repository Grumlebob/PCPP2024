// For week 5
// sestoft@itu.dk * 2014-11-14
// raup@itu.dk * 2022-11-10

// Testing performance for four lock implementations in terms of compare-and-swap:
// SimpleTryLock    -- non-reentrant tryLock and unlock
// ReentrantTryLock -- reentrant tryLock and unlock
// SimpleLock       -- non-reentrant blocking lock and unlock
// MyReentrantLock  -- reentrant blocking lock and unlock
package lecture05;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.function.IntToDoubleFunction;

// Timing a standard Java Class Library ReentrantLock
import java.util.concurrent.locks.ReentrantLock;

public class TestPerfCasLocks {
    public static void main(String[] args) {
        timeLocks();
    }

    private static void timeLocks() {
        SystemInfo();
        {
            final SimpleTryLock lock = new SimpleTryLock();
            Mark6("Untaken SimpleTryLock", (int i) -> {
                    try {
                        lock.tryLock();
                        return i;
                    } finally {
                        lock.unlock();
                    }
                });
        }
        {
            final SimpleLock lock = new SimpleLock();
            Mark6("Untaken SimpleLock", (int i) -> {
                    try {
                        lock.lock();
                        return i;
                    } finally {
                        lock.unlock();
                    }
                });
        }
        {
            final ReentrantTryLock lock = new ReentrantTryLock();
            Mark6("Untaken ReentrantTryLock", (int i) -> {
                    try {
                        lock.tryLock();
                        return i;
                    } finally {
                        lock.unlock();
                    }
                });
        }
        {
            final MyReentrantLock lock = new MyReentrantLock();
            Mark6("Untaken MyReentrantLock", (int i) -> {
                    try {
                        lock.lock();
                        return i;
                    } finally {
                        lock.unlock();
                    }
                });
        }
        {
            final ReentrantLock lock = new ReentrantLock();
            Mark6("Untaken ReentrantLock", (int i) -> {
                    try {
                        lock.lock();
                        return i;
                    } finally {
                        lock.unlock();
                    }
                });
        }
    }

    // --- Benchmarking infrastructure ---

    public static double Mark6(String msg, IntToDoubleFunction f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j=0; j<n; j++) {
                Timer t = new Timer();
                for (int i=0; i<count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
            double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
            System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
        return dummy / totalCount;
    }

    public static double Mark7(String msg, IntToDoubleFunction f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j=0; j<n; j++) {
                Timer t = new Timer();
                for (int i=0; i<count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
        double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
        System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        return dummy / totalCount;
    }

    public static void SystemInfo() {
        System.out.printf("# OS:   %s; %s; %s%n",
                          System.getProperty("os.name"),
                          System.getProperty("os.version"),
                          System.getProperty("os.arch"));
        System.out.printf("# JVM:  %s; %s%n",
                          System.getProperty("java.vendor"),
                          System.getProperty("java.version"));
        // The processor identifier works only on MS Windows:
        System.out.printf("# CPU:  %s; %d \"cores\"%n",
                          System.getenv("PROCESSOR_IDENTIFIER"),
                          Runtime.getRuntime().availableProcessors());
        java.util.Date now = new java.util.Date();
        System.out.printf("# Date: %s%n",
                          new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
    }
}

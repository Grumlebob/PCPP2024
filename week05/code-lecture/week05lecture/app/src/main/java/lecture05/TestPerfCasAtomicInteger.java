// For week 5
// sestoft@itu.dk * 2014-11-12, 2015-11-03
// raup@itu.dk * 2021-10-08
package lecture05;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

// Performance test class for MyAtomicInteger

public class TestPerfCasAtomicInteger {
    public static void main(String[] args) {
        timeSequentialGetAndAdd();
        // timeParallelGetAndAdd();
    }

    private static void timeSequentialGetAndAdd() {
        SystemInfo();
        final int count = 1_000_000;
        // This is so fast, 0.4 ns per iteration, that some kind of lock
        // coarsening or lock elision must be done in the JVM JIT.
        System.out.println(Mark7("MyAtomicInteger", (int i) -> {
                    final MyAtomicInteger ai = new MyAtomicInteger();
                    int res = 0;
                    for (int j=0; j<count; j++)
                        res += ai.addAndGet(j);
                    return res;
                }));
        System.out.println(Mark7("AtomicInteger", (int i) -> {
                    final AtomicInteger ai = new AtomicInteger();
                    int res = 0;
                    for (int j=0; j<count; j++)
                        res += ai.addAndGet(j);
                    return res;
                }));
    }

    private static void timeParallelGetAndAdd() {
        SystemInfo();
        final int count = 10_000_000, threadCount = 1;
        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1),
            stopBarrier = startBarrier;
        {
            final MyAtomicInteger ai = new MyAtomicInteger();
            for (int t=0; t<threadCount; t++) {
                new Thread(() -> {
                        try { startBarrier.await(); } catch (Exception exn) { }
                        for (int p=0; p<count; p++)
                            ai.addAndGet(p);
                        try { stopBarrier.await(); } catch (Exception exn) { }
                }).start();
            }
            try { startBarrier.await(); } catch (Exception exn) { }
            Timer t = new Timer();
            try { stopBarrier.await(); } catch (Exception exn) { }
            double time = t.check() * 1e6;
            System.out.printf("MyAtomicInteger sum = %d; time = %10.2f us; per op = %6.1f ns %n", 
                              ai.get(), time, time * 1000.0 / count / threadCount);
        }
        {
            final AtomicInteger ai = new AtomicInteger();
            for (int t=0; t<threadCount; t++) {
                new Thread(() -> {
                        try { startBarrier.await(); } catch (Exception exn) { }
                        for (int p=0; p<count; p++)
                            ai.addAndGet(p);
                        try { stopBarrier.await(); } catch (Exception exn) { }
                }).start();
            }
            try { startBarrier.await(); } catch (Exception exn) { }
            Timer t = new Timer();
            try { stopBarrier.await(); } catch (Exception exn) { }
            double time = t.check() * 1e6;
            System.out.printf("AtomicInteger   sum = %d; time = %10.2f us; per op = %6.1f ns %n", 
                              ai.get(), time, time * 1000.0 / count / threadCount);
        }
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

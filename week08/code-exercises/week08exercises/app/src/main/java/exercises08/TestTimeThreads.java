package exercises08;
// Microbenchmarks for small object creation, Thread creation, thread
// start, thread execution and join, taking an uncontended lock.
// sestoft@itu.dk * 2014-09-10, 2015-09-15
// jst@itu.dk * 2024-09-09

import java.util.concurrent.atomic.AtomicInteger;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class TestTimeThreads {

    public static void main(String[] args) {
        new TestTimeThreads();
    }

    public TestTimeThreads() {
        Benchmark.SystemInfo();
        System.out.println("--------Mark 6 measurements---------");
        final Point myMark6Point = new Point(42, 39);
        Benchmark.Mark6("Point creation",
                i -> {
                    Point p = new Point(i, i);
                    return p.hashCode();
                });
        final AtomicInteger mark6AtomicInteger = new AtomicInteger();
        Benchmark.Mark6("Thread's work",
                i -> {
                    for (int j = 0; j < 1000; j++)
                        mark6AtomicInteger.getAndIncrement();
                    return mark6AtomicInteger.doubleValue();
                });
        Benchmark.Mark6("Thread create",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j = 0; j < 1000; j++)
                            mark6AtomicInteger.getAndIncrement();
                    });
                    return t.hashCode();
                });
        Benchmark.Mark6("Thread create start",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j = 0; j < 1000; j++)
                            mark6AtomicInteger.getAndIncrement();
                    });
                    t.start();
                    return t.hashCode();
                });
        Benchmark.Mark6("Thread create start join",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j = 0; j < 1000; j++)
                            mark6AtomicInteger.getAndIncrement();
                    });
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException exn) {
                    }
                    return t.hashCode();
                });
        System.out.printf("mark6AtomicInteger value = %d%n", mark6AtomicInteger.intValue());
        final Object mark6Obj = new Object();
        Benchmark.Mark6("Uncontended lock",
                i -> {
                    synchronized (mark6Obj) {
                        return i;
                    }
                });

        System.out.println("-----------Mark 7 measurements--------------");

        final Point myPoint = new Point(42, 39);

        // Point creation benchmark using Mark7
        Benchmark.Mark7("Point creation", i -> {
            Point p = new Point(i, i);
            return p.hashCode();
        });

        final AtomicInteger ai = new AtomicInteger();

        // Thread's work benchmark using Mark7
        Benchmark.Mark7("Thread's work", i -> {
            for (int j = 0; j < 1000; j++)
                ai.getAndIncrement();
            return ai.doubleValue();
        });

        // Thread creation benchmark using Mark7
        Benchmark.Mark7("Thread create", i -> {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    ai.getAndIncrement();
            });
            return t.hashCode();
        });

        // Thread create and start benchmark using Mark7
        Benchmark.Mark7("Thread create start", i -> {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    ai.getAndIncrement();
            });
            t.start();
            return t.hashCode();
        });

        // Thread create, start, and join benchmark using Mark7
        Benchmark.Mark7("Thread create start join", i -> {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++)
                    ai.getAndIncrement();
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException exn) {
                exn.printStackTrace();
            }
            return t.hashCode();
        });

        // Uncontended lock benchmark using Mark7
        final Object obj = new Object();
        Benchmark.Mark7("Uncontended lock", i -> {
            synchronized (obj) {
                return i;
            }
        });

        System.out.printf("ai value = %d%n", ai.intValue());


    }
}

/**
 * Immutable Point class used by DelegatingVehicleTracker
 *
 * @author Brian Goetz and Tim Peierls
 */
class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

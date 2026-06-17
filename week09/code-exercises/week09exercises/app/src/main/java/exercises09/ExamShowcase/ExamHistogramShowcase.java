package exercises09.ExamShowcase;

import exercises09.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static benchmarking.Benchmark.Mark7;

class ExamHistogramShowcase {
    public static void main(String[] args) {

        //Shared data: Histogram.bins
        // Synchronization:
        // -- Monitor pattern
        // -- Lock-free (CAS)
        // -- Lock Striping
        // -- Actor model
        int noThreads = 8; //Remember some histograms spawn their own threads. So this is not total number of threads
        int range = 100000;

        //--PERFORMANCE TEST: Compares Performance on different histograms. With monitor pattern as baseline--

        // NO THREADS
        Mark7("Sequential Test - MonitorHistogram", i -> {
            MonitorHistogram monitorHistogram = new MonitorHistogram(30, 0);
            sequentialHistogram(monitorHistogram, range);
            return 0.0;
        });

        Mark7("Sequential Test - CasHistogram", i -> {
            CasHistogram casHistogram = new CasHistogram(30, 0);
            sequentialHistogram(casHistogram, range);
            return 0.0;
        });

        Mark7("Sequential Test - LockStripedHistogram", i -> {
            LockStripedHistogram lockStripedHistogram = new LockStripedHistogram(30, 0);
            sequentialHistogram(lockStripedHistogram, range);
            return 0.0;
        });

        Mark7("Sequential Test - ActorHistogram", i -> {
            ActorHistogram histogram = new ActorHistogram(30, 0);
            sequentialHistogram(histogram, range);
            histogram.shutdown();
            return 0.0;
        });

        // WITH 8 THREADS
        Mark7("Parallel Test - MonitorHistogram", i -> {
            MonitorHistogram monitorHistogram = new MonitorHistogram(30, 0);
            parallelHistogram(range, noThreads, monitorHistogram);
            return 0.0;
        });

        Mark7("Parallel Test - CasHistogram", i -> {
            CasHistogram casHistogram = new CasHistogram(30, 0);
            parallelHistogram(range, noThreads, casHistogram);
            return 0.0;
        });


        Mark7("Parallel Test - LockStripedHistogram", i -> {
            LockStripedHistogram lockStripedHistogram = new LockStripedHistogram(30, 0);
            parallelHistogram(range, noThreads, lockStripedHistogram);
            return 0.0;
        });

        Mark7("Parallel Test - ActorHistogram", i -> {
            ActorHistogram histogram = new ActorHistogram(30, 0);
            parallelHistogram(range, noThreads, histogram);
            histogram.shutdown();
            return 0.0;
        });

        // WITH HIGHER CONTENTION
        int contentionLevel = 10000;  // More CPU work = More waiting on the lock / CAS will have more outdated values.
        int loweredBuckets = 17; // more contention on each bucket
        int moreThreadsThanCores = 32; // More threads = More threads will try to access the same bucket / in CAS overwrite each other's values
        Mark7("Parallel Contention Test - MonitorHistogram", i -> {
            MonitorHistogram monitorHistogram = new MonitorHistogram(loweredBuckets, contentionLevel);
            parallelHistogram(range, moreThreadsThanCores, monitorHistogram);
            return 0.0;
        });

        Mark7("Parallel Contention Test - CasHistogram", i -> {
            CasHistogram casHistogram = new CasHistogram(loweredBuckets, contentionLevel);
            parallelHistogram(range, moreThreadsThanCores, casHistogram);
            return 0.0;
        });

        Mark7("Parallel Contention Test - LockStripedHistogram", i -> {
            LockStripedHistogram lockStripedHistogram = new LockStripedHistogram(loweredBuckets, contentionLevel);
            parallelHistogram(range, moreThreadsThanCores, lockStripedHistogram);
            return 0.0;
        });

        Mark7("Parallel Contention Test - ActorHistogram", i -> {
            ActorHistogram histogram = new ActorHistogram(17, contentionLevel);
            parallelHistogram(range, moreThreadsThanCores, histogram);
            histogram.shutdown();
            return 0.0;
        });

        //--PERFORMANCE TEST: With own synchronization technique as baseline. Checks scalability--
        int span = 30;
        int numberOfIncrements = 1000000;
        Random rng = new Random();
        List<Integer> randomList = rng.ints(0, span).limit(numberOfIncrements).boxed().collect(Collectors.toUnmodifiableList());

        for (int threads = 1; threads <= 32; threads *= 2) {
            final int threadCount = threads;
            System.out.println("THREAD COUNT: " + threadCount);

            Mark7("MonitorHistogram with " + threadCount + " threads", i -> {
                MonitorHistogram monitorHistogram = new MonitorHistogram(span, 0);
                incrementRandomBinsWithNThreads(randomList, threadCount, monitorHistogram);
                return 0.0;
            });

            Mark7("CasHistogram with " + threadCount + " threads", i -> {
                CasHistogram casHistogram = new CasHistogram(span, 0);
                incrementRandomBinsWithNThreads(randomList, threadCount, casHistogram);
                return 0.0;
            });

            Mark7("LockStripedHistogram with " + threadCount + " threads", i -> {
                LockStripedHistogram lockStripedHistogram = new LockStripedHistogram(span, 0);
                incrementRandomBinsWithNThreads(randomList, threadCount, lockStripedHistogram);
                return 0.0;
            });

            Mark7("ActorHistogram with " + threadCount + " threads", i -> {
                ActorHistogram histogram = new ActorHistogram(span, 0);
                incrementRandomBinsWithNThreads(randomList, threadCount, histogram);
                histogram.shutdown();
                return 0.0;
            });
        }
    }

    private static void sequentialHistogram(Histogram histogram, int range) {
        for (int i = 0; i < range; i++) {
            int factors = histogram.countFactors(i);
            histogram.increment(factors);
        }
    }

    private static void parallelHistogram(int range, int threadCount, Histogram histogram) {
        final int perThread = range / threadCount;
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t;
            final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] = new Thread(() -> {
                for (int i = from; i < to; i++) {
                    histogram.increment(histogram.countFactors(i));
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException exn) {
            Thread.currentThread().interrupt();
        }
    }

    private static void incrementRandomBinsWithNThreads(List<Integer> randomList, int threadCount, Histogram histogram) {
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            int threadN = t;
            threads[t] = new Thread(() -> {
                for (int i = randomList.size() * threadN; i < randomList.size() / threadCount * (threadN+1); i++) {
                    histogram.increment(randomList.get(i));
                }
            });
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException exn) {
            Thread.currentThread().interrupt();
        }
        assert IntStream.range(0, histogram.getSpan()).map(histogram::getCount).sum() == randomList.size();
    }
}

// For week 4
// raup@itu.dk * 2023-09-16

package lecture04;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class CounterTest {

    private Counter count;
    private CyclicBarrier barrier;
    // You may use a Thread Pool to increase the performance of the test (Thread Pools will be explained in week 09)
    // private final static ExecutorService pool = Executors.newCachedThreadPool();


    @BeforeEach
    public void initialize() {
        count = new CounterDR();
        // count = new CounterSync();
        // count = new CounterAto();
    }



    // @Test
    // @Disabled
    @DisplayName("Counter Sequential")
    public void testingCounterSequential() {
        System.out.printf("Sequential counter tests with 10000 iterations");
        int localSum = 0;
        for (int i = 0; i < 10_000; i++) {
            count.inc();
            localSum++;
        }
        assertTrue(count.get()==localSum);
    }



    @ParameterizedTest
    @Disabled
    @DisplayName("Counter Parallel")
    @MethodSource("argsGeneration")
    public void testingCounterParallel(int nrThreads, int N) {
        System.out.printf("Parallel counter tests with %d threads and %d iterations",
                          nrThreads,N);

        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        // start threads
        for (int i = 0; i < nrThreads; i++) {
            new Turnstile(N).start();
        }

        try {
            barrier.await(); // wait until threads are ready for execution (maximize contention)
            barrier.await(); // wait for threads to finish
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        assertTrue(N*nrThreads == count.get(), "count.get() == "+count.get()+", but we expected "+N*nrThreads);
    }

    private static List<Arguments> argsGeneration() {

        // Max number of increments
        final int I = 50_000;
        final int iInit = 10_000;
        final int iIncrement = 10_000;

        // Max exponent number of threads (2^J)
        final int J = 6;
        final int jInit = 1;
        final int jIncrement = 1;

        // List to add each parameters entry
        List<Arguments> list = new ArrayList<Arguments>();

        // Loop to generate each parameter entry
        // (2^j, i) for j \in {100,200,...,J} and j \in {1,..,I}
        for (int i = iInit; i <= I; i += iIncrement) {
            for (int j = jInit; j < J; j += jIncrement) {
                list.add(Arguments.of((int) Math.pow(2,j),i));
            }
        }

        // Return the list
        return list;
    }


    @RepeatedTest(1000)
    // @Test
    // @Disabled
    @DisplayName("Counter Parallel Constant (2 Threads, 10 increments)")
    public void testingCounterParallelConstant() {
        final int nrThreads = 2;
        final int N = 50;

        // init barrier
        barrier = new CyclicBarrier(nrThreads+1);

        // start threads
        for (int j = 0; j < nrThreads; j++) {
            // As an example, here we do not use the Turnstile class
            // we explicitly define and start threads within the body of the test
            new Thread(() -> {
                    try {
                        barrier.await(); // waits until all threads all ready
                        for (int i = 0; i < N; i++) {
                            count.inc();
                        }
                        barrier.await(); // waits until all threads are done (needed for the main thread to know when all threads are finished)
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

            }).start();
        }

        try {
            barrier.await(); // wait until threads are ready for execution (maximize contention)
            barrier.await(); // wait for threads to finish
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        // Assert property
        assertTrue(N*nrThreads == count.get(), "count.get() == "+count.get()+", but we expected "+N*nrThreads);
    }


    /*** Test threads ***/
    public class Turnstile extends Thread {

        private final int N;

        public Turnstile(int N) { this.N = N; }

        public void run() {
            try {
                barrier.await(); // waits until all threads all ready
                for (int i = 0; i < N; i++) {
                    count.inc();
                }
                barrier.await(); // waits until all threads are done (needed for the main thread to know when all threads are finished)
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}

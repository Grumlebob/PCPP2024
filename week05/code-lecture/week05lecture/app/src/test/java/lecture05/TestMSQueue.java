package lecture05;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.atomic.AtomicInteger;


public class TestMSQueue {

    private UnboundedQueue<Integer> buffer;
    private CyclicBarrier barrier;

    // These are two auxiliary variables for the test
    // The validity of the test relies on the correct
    // implementation of the AtomicInteger class
    private AtomicInteger putSum;
    private AtomicInteger takeSum;


    @BeforeEach
    public void initialize() {
        putSum  = new AtomicInteger(0);
        takeSum = new AtomicInteger(0);

        // buffer  = new LockingQueue<Integer>();
        buffer  = new MSQueue<Integer>();
        // buffer  = new MSQueueRefl<Integer>();
    }

    @ParameterizedTest
    @DisplayName("PutTakeTest of Unbouded buffer (MS queues)")
    @MethodSource("argsProvider")
    public void putTakeTest(int nrThreads,
                            int nrTrials) {
        System.out.printf("Running test with parameters: (%d,%d)\n",
                          nrThreads, nrTrials);

        // init barrier
        barrier = new CyclicBarrier((nrThreads*2) + 1);

        for (int i = 0; i < nrThreads; i++) {
            new Producer(nrTrials).start();
            new Consumer(nrTrials).start();
        }

        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        int ts = takeSum.get();
        int ps = putSum.get();

        System.out.printf("Outcome: takeSum.get() == %d, putSum.get()== %d\n",
                          ts,ps);

        // Property: Since producers only add positive integers, a
        // correct implementation should at least ensure that the sum
        // of consumed integers is less than the sum of produced
        // integers; we cannot assert equality as consumers/producers
        // do not wait for each other as required in the traditional
        // producer consumer problem (see material for weeks 3-4).
        assert(ts <= ps);
    }

    private static List<Arguments> argsProvider() {

        // Max number of trials
        final int I = 200;
        final int iInit = 50;
        final int iIncrement = 50;

        // Max exponent number of threads (2^J)
        final int J = 5;
        final int jInit = 1;
        final int jIncrement = 1;

        // List to add each parameters entry
        List<Arguments> list = new ArrayList<Arguments>();

        // Loop to generate each parameter entry
        // (2^j, i, k) for i \in {50,100,...,J} and j \in {1,..,I} and k \in {20,40,...,K}
        for (int i = iInit; i <= I; i += iIncrement) {
            for (int j = jInit; j < J; j += jIncrement) {
                list.add(Arguments.of((int) Math.pow(2,j), i));
            }
        }

        // Return the list
        return list;
    }





    /**** Threads to test ****/
    class Producer extends Thread {
        int nrTrials;
        int localSum;

        public Producer(int nrTrials) {
            this.nrTrials = nrTrials;
            this.localSum = 0;
        }

        public void run() {
            try {
                barrier.await();
                for (int i = 0; i < nrTrials; i++) {
                    Random r  = new Random();
                    int toPut = r.nextInt(1000); // get a random positive integer in (1,1000)
                    buffer.enqueue(toPut);
                    localSum += toPut;
                }
                putSum.addAndGet(localSum);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    class Consumer extends Thread {
        int nrTrials;
        int localSum;

        public Consumer(int nrTrials) {
            this.nrTrials = nrTrials;
            this.localSum = 0;
        }

        public void run() {
            try {
                barrier.await();
                for (int i = 0; i < nrTrials; i++) {
                    Integer j = buffer.dequeue();
                    if (j != null) 
                        localSum += j;
                }
                takeSum.addAndGet(localSum);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}

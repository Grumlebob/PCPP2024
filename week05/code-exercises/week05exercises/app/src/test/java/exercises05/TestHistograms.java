package exercises05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHistograms {

    private CasHistogram casHistogram;
    private Histogram1 sequentialHistogram;
    private ConcurrentTestRunner testRunner;

    @BeforeEach
    public void setup() {
        int span = 30;
        casHistogram = new CasHistogram(span);
        sequentialHistogram = new Histogram1(span);
        testRunner = new ConcurrentTestRunner(5000);  // 5000 threads for parallel tests
    }

    // Function to count the number of prime factors of a number `n`
    private static int countPrimeFactors(int n) {
        if (n < 2) return 0;  // No prime factors for numbers less than 2

        int primeFactorCount = 0;
        int divisor = 2;

        // Check for prime factors from divisor upwards
        while (n >= divisor * divisor) {
            if (n % divisor == 0) {
                primeFactorCount++;
                n /= divisor;  // Divide by divisor to reduce the number
            } else {
                divisor++;  // Move to the next possible divisor
            }
        }

        primeFactorCount++;  // n itself is a prime factor if n > 1

        return primeFactorCount;
    }


    @Test
    public void testSequentialHistogram() {
        for (int i = 0; i < 5000; i++) {
            int factors = countPrimeFactors(i);
            casHistogram.increment(factors);
            sequentialHistogram.increment(factors);
        }

        for (int i = 0; i < 30; i++) {
            assertEquals(sequentialHistogram.getCount(i), casHistogram.getCount(i), "Mismatch in bin " + i);
        }
    }

    @Test
    public void testParallelHistogram() throws InterruptedException {
        int range = 5000;

        //using concurrent CasHistogram
        testRunner.runConcurrently(() -> {
            int threadId = (int) Thread.currentThread().getId() % range;  // Ensure each thread processes a unique number
            int factors = countPrimeFactors(threadId);
            casHistogram.increment(factors);
        });

        //Using sequential Histogram
        for (int i = 0; i < range; i++) {
            int factors = countPrimeFactors(i);
            sequentialHistogram.increment(factors);
        }

        //Checking if the results are the same
        for (int i = 0; i < 30; i++) {
            assertEquals(sequentialHistogram.getCount(i), casHistogram.getCount(i), "Mismatch in bin " + i);
        }
    }


    @Test
    public void testGetAndClear() {
        casHistogram.increment(5);
        casHistogram.increment(5);
        casHistogram.increment(5);

        assertEquals(3, casHistogram.getCount(5), "Initial count for bin 5 should be 3");
        assertEquals(3, casHistogram.getAndClear(5), "getAndClear should return 3 for bin 5");
        assertEquals(0, casHistogram.getCount(5), "After getAndClear, bin 5 should be reset to 0");
    }

}

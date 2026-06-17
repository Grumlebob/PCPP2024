package exercises08;

import benchmarking.Benchmark;

public class TestVolatile {
    private volatile int vCtr;  // Volatile counter
    private int ctr;  // Non-volatile counter

    public void vInc() {
        vCtr++;
    }

    public void inc() {
        ctr++;
    }

    public int getVolatile() {
        return vCtr;
    }

    public int getNonVolatile() {
        return ctr;
    }

    // Main method to execute the benchmark
    public static void main(String[] args) {
        new TestVolatile().runBenchmark();
    }

    public void runBenchmark() {
        Benchmark.SystemInfo();

        // Benchmark volatile increment
        Benchmark.Mark7("Volatile Increment", i -> {
            for (int j = 0; j < 1000; j++) {
                vInc();
            }
            return getVolatile();
        });

        // Benchmark non-volatile increment
        Benchmark.Mark7("Non-Volatile Increment", i -> {
            for (int j = 0; j < 1000; j++) {
                inc();
            }
            return getNonVolatile();
        });
    }
}

package exercises08;
// jst@itu.dk * 2023-09-05

import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class TestTimeSearch {
    public static void main(String[] args) {
        new TestTimeSearch();
    }

    public TestTimeSearch() {
        final String filename = "long-text-file.txt"; // File in the resources folder
        final String target = "ipsum";

        final PrimeCounter lc = new PrimeCounter();  //name is a bit misleading, it is just a counter
        String[] lineArray = readWords(filename);

        System.out.println("Array Size: " + lineArray.length);
        System.out.println("# Occurences of " + target + " :" + search(target, lineArray, 0, lineArray.length, lc));

        // Sequential Search Benchmark
        Benchmark.Mark7("Sequential Search", i -> search(target, lineArray, 0, lineArray.length, lc));

        // Parallel Search Benchmarks with varying number of threads
        for (int N = 1; N <= 16; N *= 2) {  // Test with 1, 2, 4, 8, and 16 threads
            PrimeCounter lcParallel = new PrimeCounter();
            int finalN = N;
            Benchmark.Mark7("Parallel Search with " + finalN + " threads", i -> countParallelN(target, lineArray, finalN, lcParallel));
        }
    }

    static long search(String x, String[] lineArray, int from, int to, PrimeCounter lc) {
        //Search each line of file
        for (int i = from; i < to; i++) lc.add(linearSearch(x, lineArray[i]));
        //System.out.println("Found: "+lc.get());
        return lc.get();
    }

    static int linearSearch(String x, String line) {
        //Search for occurences of c in line
        String[] arr = line.split(" ");
        int count = 0;
        for (int i = 0; i < arr.length; i++) if ((arr[i].equals(x))) count++;
        return count;
    }

    // Parallel search method using N threads
    private static long countParallelN(String target, String[] lineArray, int N, PrimeCounter lc) {
        final int perThread = lineArray.length / N;
        Thread[] threads = new Thread[N];

        // Shared counter for tracking occurrences
        AtomicLong totalCount = new AtomicLong(0);

        for (int t = 0; t < N; t++) {
            final int from = t * perThread;
            final int to = (t == N - 1) ? lineArray.length : (t + 1) * perThread;

            // Create a thread for each section of the array
            threads[t] = new Thread(() -> {
                long localCount = 0;
                for (int i = from; i < to; i++) {
                    localCount += linearSearch(target, lineArray[i]);
                }
                totalCount.addAndGet(localCount); // Add the result to the shared counter
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return totalCount.get(); // Return the total count of occurrences
    }

    public static String[] readWords(String filename) {
        try {
            InputStream inputStream = TestTimeSearch.class.getClassLoader().getResourceAsStream(filename);
            if (inputStream == null) {
                throw new IOException("File not found in classpath: " + filename);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().toArray(String[]::new);
        } catch (IOException exn) {
            exn.printStackTrace();
            return null;
        }
    }

}

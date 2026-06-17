package exercises09;

import static benchmarking.Benchmark.Mark7;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestCountPrimesWithFutures {

	public static void main(String[] args) {
		final int range = 100_000;

		// Sequential benchmark
		Mark7("countSequential", i -> countSequential(range));

		// Parallel benchmark with futures
		for (int threadCount = 1; threadCount <= 16; threadCount++) {
			final int threads = threadCount;
			Mark7(String.format("countParallelWithFutures %2d", threads), i -> countParallelWithFutures(range, threads));
		}
	}

	// Sequential solution
	private static long countSequential(int range) {
		long count = 0;
		for (int i = 0; i < range; i++) {
			if (isPrime(i)) count++;
		}
		return count;
	}

	// Parallel solution using futures
	private static long countParallelWithFutures(int range, int threadCount) {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<Future<Integer>> results = new ArrayList<>();

		// Divide the range across threads and submit tasks
		final int perThread = range / threadCount;
		for (int i = 0; i < threadCount; i++) {
			final int start = i * perThread;
			final int end = (i + 1 == threadCount) ? range : start + perThread;
			results.add(executor.submit(() -> countPrimes(start, end)));
		}

		// Collect results from futures
		long totalPrimes = 0;
		try {
			for (Future<Integer> result : results) {
				//Notice, get here is a blocking call
				//That is how we know that all results will be ready.
				totalPrimes += result.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
		return totalPrimes;
	}

	// Count primes in a given range
	private static int countPrimes(int start, int end) {
		int count = 0;
		for (int i = start; i < end; i++) {
			if (isPrime(i)) count++;
		}
		return count;
	}

	// Prime-checking function
	private static boolean isPrime(int n) {
		if (n <= 1) return false;
		for (int i = 2; i * i <= n; i++) {
			if (n % i == 0) return false;
		}
		return true;
	}
}

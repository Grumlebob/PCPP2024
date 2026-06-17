package lecture08;
// Week 3
// Counting primes, using multiple threads for better performance.
// (Much simplified from CountprimesMany.java)
// sestoft@itu.dk * 2014-08-31, 2015-09-15
// modified rikj@itu.dk 2017-09-20
// modified jst@itu.dk 2023-08-31

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class PrimesCompare {
  public static void main(String[] args) { new PrimesCompare(); }

  public PrimesCompare() {
    Benchmark.SystemInfo();
    final int range = 100_000;
    System.out.println("# Primes: "+countPrimes(range)+" fast alg");
    System.out.println("# Primes: "+countPrimesS(range)+" slow alg");
    Benchmark.Mark7("countPrimes", i -> countPrimes(range));
    Benchmark.Mark7("countPrimesS", i -> countPrimesS(range));
  }

  // Fast solution
  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }

  // Slow solution
  private static boolean isPrimeS(int n) {
    int k = 2;
    while (k < n && n % k != 0)
      k++;
    return n >= 2 && k >= n;
  }

  // Timing fast solution
  private static int countPrimes(int range) {
    int count = 0;
    final int from = 0, to = range;
    for (int i=from; i<to; i++)
      if (isPrime(i)) count++;
    return count;
  }

  // Timing slow solution
  private static int countPrimesS(int range) {
    int count = 0;
    final int from = 0, to = range;
    for (int i=from; i<to; i++)
      if (isPrimeS(i)) count++;
    return count;
  }
}



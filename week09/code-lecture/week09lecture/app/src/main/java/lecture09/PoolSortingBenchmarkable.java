package lecture09;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.IntToDoubleFunction;
import java.util.*;
import java.util.concurrent.TimeUnit;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class PoolSortingBenchmarkable{
  private static int threshold= 100; 
  private static int pSize= 100_000; 
  private static int maxPool= 17;
  // sorting problems smaller than threshold are not subdivided

  public static void main(String[] args){ new PoolSortingBenchmarkable(); }

  public PoolSortingBenchmarkable() {
    for (int n= 1; n < maxPool; n= 2*n) {
      //final ExecutorService pool = Executors.newFixedThreadPool(n);
      //final ExecutorService pool = Executors.newWorkStealingPool();
      //final ExecutorService pool = Executors.newCachedThreadPool();
      final ExecutorService pool= new ForkJoinPool(n);

      runSize(pool, pSize, threshold, n); 
    }
  }

  private static void runSize(ExecutorService pool, int pSize, int threshold, int n) {
    final int[] intArray= fillIntArray(pSize);
    Benchmark.Mark8Setup("Quicksort Executor", String.format("%2d", n),
      new Benchmarkable() { 

        public void setup() {
          shuffle(intArray);
        }

        public double applyAsDouble(int i) {
          Future<?> done= pool.submit(new QuickSortTask(intArray, 0, pSize-1, pool, threshold));
          PoolFinish(done);
          //testSorted(intArray); //only needed while testing
          return 0.0;
        }
      }
    );
  }

  public static void PoolFinish(Future<?> done) {
    try {  done.get();  }
    catch (InterruptedException | ExecutionException e) { e.printStackTrace(); } 
  }
 
  // Quicksort utils
  private static void swap(int[] arr, int s, int t) {
    int tmp = arr[s];  arr[s] = arr[t];  arr[t] = tmp;
  }

  private static final java.util.Random rnd = new java.util.Random();
  public static void shuffle(int[] arr) {
    for (int i = arr.length-1; i > 0; i--)
    swap(arr, i, rnd.nextInt(i+1));
  }

  public static int[] fillIntArray(int n) {
    int [] arr = new int[n];
     for (int i = 0; i < n; i++) arr[i] = i;
    return arr;
  }

  public static void testSorted(int[] a) {
    int c= 0;
    while ( c < (a.length-1) ) {
      if (a[c] <= a[c+1]) {  c= c+1; }
      else { System.out.println("Error at "+c); break; }
    }
    //a is ordered
    if (c == a.length-1) System.out.println("Success!"); 
  }
}

  

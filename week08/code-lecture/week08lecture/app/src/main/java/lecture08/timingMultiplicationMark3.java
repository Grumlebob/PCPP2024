package lecture08;
// Timing multiplication taken from BenchMark.java

import benchmarking.Timer;
import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

class timingMultiplicationMark3 {
  public static void main(String[] args) {
	  Benchmark.SystemInfo();
    Benchmark.Mark3();
  }

  // ========== Example functions and benchmarks ==========

  private static double multiply(int i) {
    double x = 1.1 * (double)(i & 0xFF);
    //double x = 1.1 * (double)(2 & 0xFF);
    return x * x * x * x * x * x * x * x * x * x 
          * x * x * x * x * x * x * x * x * x * x;
  }
}

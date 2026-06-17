package lecture08;
// Timing multiplication
// jst@itu.dk vers 07/10 * 2024

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class PerfMult { 
  public static void main(String[] args) { new PerfMult(); }
  public PerfMult() {
     Benchmark.Mark6("Multiplication", i -> { 
        return i*i; 
    });
  } 
}
package lecture08;
// Timing Volatile
// jst@itu.dk vers 09/10 * 2021

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class PerfTest { 
  public static void main(String[] args) { new PerfTest(); }
  public PerfTest() {
     Benchmark.Mark6("Non volatile incr", i -> { 
      inc();
      return 0.0; 
    });

    Benchmark.Mark6("volatile incr", i -> { 
      vInc();
      return 0.0; 
    });
  } 
  private volatile int vCtr;
  private int ctr;

  public void vInc () { vCtr++; } 
  public void inc () { ctr++; }
}
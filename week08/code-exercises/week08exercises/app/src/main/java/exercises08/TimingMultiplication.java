package exercises08;
// Timing multiplication taken from BenchMark.java
// Code to be used in class08, updated by jst@itu.dk 09/04/2024
import benchmarking.Benchmark;
import benchmarking.Benchmarkable;
import benchmarking.Timer;

class TimingMultiplication {
  public static void main(String[] args) { new TimingMultiplication(); }
  
  public TimingMultiplication() {
    Benchmark.SystemInfo();
    Benchmark.Mark2();
  }
}

package exercises09;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022
// jst@itu.dk * 04/10/2023

public interface Histogram {
  public void increment(int bin);
  public int getCount(int bin);
  public int getSpan();

  // Default method to count factors of p
  default int countFactors(int p) {
    if (p < 2) return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
        factorCount++;
        p = p / k;
      } else {
        k = k + 1;
      }
    }
    return factorCount;
  }

  // Default method to check if two Histograms have identical counts
  default boolean equalsHistogram(Histogram other) {
    if (other == null) {
      return false;
    }
    // Must have same number of bins
    if (this.getSpan() != other.getSpan()) {
      return false;
    }
    // Compare the count in each bin
    for (int i = 0; i < this.getSpan(); i++) {
      if (this.getCount(i) != other.getCount(i)) {
        return false;
      }
    }
    return true;
  }

  default void SimulateCPUWork(int contentionLevel) {
    if (contentionLevel != 0) {
      var simulatedCpuCalculation = Math.pow(2, contentionLevel);
      for (int i = 0; i < contentionLevel; i++) {
        simulatedCpuCalculation = Math.pow(2, i);
      }
      if (simulatedCpuCalculation == 7) {
        System.out.println("This is to enforce contention, so compiler ain't optimizing the code");
      }
    }
  }

}

// jst@itu.dk * 04/10/2023
package exercises09;

public class MonitorHistogram implements Histogram {
    private final int[] counts;
    private int contentionLevel = 0;

    public MonitorHistogram(int span, int contentionLevel) {
        this.counts = new int[span];
        this.contentionLevel = contentionLevel;
    }

    public synchronized void increment(int bin) {
        SimulateCPUWork(contentionLevel);
        counts[bin] = counts[bin] + 1;
    }

    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }
}

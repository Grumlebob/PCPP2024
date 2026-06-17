package exercises09;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] bins;
    private int contentionLevel = 0;

    public CasHistogram(int span, int contentionLevel) {
        this.bins = new AtomicInteger[span];
        this.contentionLevel = contentionLevel;
        for (int i = 0; i < span; i++) {
            bins[i] = new AtomicInteger(0);
        }
    }

    //Lock-free
    @Override
    public void increment(int bin) {
        AtomicInteger currentBin = bins[bin];
        int oldValue, newValue;
        do {
            oldValue = currentBin.get();
            newValue = oldValue + 1;

            SimulateCPUWork(contentionLevel);

        } while (!currentBin.compareAndSet(oldValue, newValue));
    }

    //Wait-free
    @Override
    public int getCount(int bin) {
        return bins[bin].get();
    }

    //Wait-free
    @Override
    public int getSpan() {
        return bins.length;
    }

    //Lock free
    public int getAndClear(int bin) {
        AtomicInteger currentBin = bins[bin];
        int oldValue;
        do {
            oldValue = currentBin.get();
        } while (!currentBin.compareAndSet(oldValue, 0));
        return oldValue;
    }
}

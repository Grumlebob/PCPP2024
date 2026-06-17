package exercises09;

import java.util.concurrent.ConcurrentHashMap;

public class LockStripedHistogram implements Histogram {

    // The ConcurrentHashMap internally uses segmented locks ("lock striping") to manage concurrency
    private final ConcurrentHashMap<Integer, Integer> bins;
    private final int contentionLevel;

    public LockStripedHistogram(int span, int contentionLevel) {
        this.bins = new ConcurrentHashMap<>(span);
        // Initialize bins
        for (int i = 0; i < span; i++) {
            bins.put(i, 0);
        }
        this.contentionLevel = contentionLevel;
    }

    @Override
    public void increment(int bin) {
        // compute() holds lock for the 'bin'
        bins.compute(bin, (key, oldValue) -> {
            // oldValue may be null if not present yet
            int currentVal = (oldValue == null) ? 0 : oldValue;

            SimulateCPUWork(contentionLevel);

            return currentVal + 1;
        });
    }

    @Override
    public int getCount(int bin) {
        return bins.getOrDefault(bin, 0);
    }

    @Override
    public int getSpan() {
        return bins.size();
    }
}

// jst@itu.dk * 04/10/2023
package exercises09.ExamShowcase;

import exercises09.Histogram;

public class ReadWriteHistogram implements Histogram {
    private final CustomReaderWriterLock rwLock = new CustomReaderWriterLock();
    private int[] counts;
    private int total = 0;

    public ReadWriteHistogram(int span) {
        synchronized (this) {
            this.counts = new int[span];
        }
    }

    public void increment(int bin) {
        rwLock.writeLock();
        try {
            counts[bin] = counts[bin] + 1;
            total++;
        } finally {
            rwLock.writeUnlock();
        }
    }

    public int getCount(int bin) {
        rwLock.readLock();
        try {
            return counts[bin];
        } finally {
            rwLock.readUnlock();
        }
    }

    public int getSpan() {
        return counts.length;
    }

    public int getTotal() {
        rwLock.readLock();
        try {
            return total;
        } finally {
            rwLock.readUnlock();
        }
    }
}

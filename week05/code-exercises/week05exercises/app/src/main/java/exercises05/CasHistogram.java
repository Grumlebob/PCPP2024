package exercises05;

import java.util.concurrent.atomic.AtomicInteger;

class CasHistogram implements Histogram {

    private final AtomicInteger[] bins;

    public CasHistogram(int span) {
        this.bins = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            bins[i] = new AtomicInteger(0);
        }
    }

    //increment(7) will add one to bin 7
    @Override
    public void increment(int bin) {
        //Example with increment(5)
        //currentBin = bins[5] = 6
        AtomicInteger currentBin = bins[bin];
        int oldValue, newValue;
        do {
            //gets 6
            oldValue = currentBin.get();
            //newValue = 6 + 1 = 7
            newValue = oldValue + 1;
            //If another thread has changed the value of currentBin
            //we will go back into do-while loop
        } while (!currentBin.compareAndSet(oldValue, newValue));
    }

    //getCount(7) will return the current count in bin 7
    @Override
    public int getCount(int bin) {
        return bins[bin].get();
    }

    //getSpan() will return the number of bins
    @Override
    public int getSpan() {
        return bins.length;
    }

    //getAndClear(7) returns the current count in bin 7 and reset the count to 0.
    @Override
    public int getAndClear(int bin) {
        //Example with getAndClear(5)
        //currentBin = bins[5] = 6
        AtomicInteger currentBin = bins[bin];
        int oldValue;
        do {
            //gets 6
            oldValue = currentBin.get();
            //while 6 is not 0, set 0
            //unless another thread has changed incremented the value,
            //to something like 7
            //we will go back into do-while loop
            //oldValue will be 7, and we try again.
        } while (!currentBin.compareAndSet(oldValue, 0));
        //return 6
        return oldValue;
    }
}

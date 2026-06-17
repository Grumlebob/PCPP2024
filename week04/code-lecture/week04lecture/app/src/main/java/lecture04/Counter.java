// For week 4
// raup@itu.dk * 2023-09-16

package lecture04;

import java.util.concurrent.atomic.AtomicInteger;


public interface Counter {
    public void inc();
    public int get();
}


class CounterDR implements Counter {

    private int count;

    public CounterDR() {
        count = 0;
    }

    public void inc() {
        count++;
    }

    public int get() {
        return count;
    }
}

class CounterSync implements Counter {

    private int count;

    public CounterSync() {
        count = 0;
    }

    public synchronized void inc() {
        count++;
    }

    public int get() {
        return count;
    }
}

class CounterAto implements Counter {

    private AtomicInteger count;

    public CounterAto() {
        count = new AtomicInteger(0);
    }

    public void inc() {
        count.incrementAndGet();
    }

    public int get() {
        return count.get();
    }
}

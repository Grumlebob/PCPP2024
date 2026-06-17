// For week 5
// sestoft@itu.dk * 2014-11-14
package lecture05;

import java.util.concurrent.atomic.AtomicReference;

// Reentrant lock supporting only tryLock and unlock

// Only the owner thread ever accesses the holdCount field (so it
// actually does not need to be volatile?!).  Only the owner thread
// ever changes the reference in holder, to null, or from null, and
// then only in the atomic act of becoming the owner.

public class ReentrantTryLock {
    // Refers to holding thread, null iff unheld
    private final AtomicReference<Thread> holder = new AtomicReference<Thread>();
    // Valid only if holder != null
    private volatile int holdCount = 0;

    public boolean tryLock() {
        final Thread current = Thread.currentThread();
        if (holder.get() == current) {    // already held by this thread
            holdCount++;
            return true;
        } else if (holder.compareAndSet(null, current)) {
            holdCount = 1;    // was unheld and we got it
            return true;
        }
        return false;       // already held, or just grabbed, by other thread
    }

    public void unlock() {
        final Thread current = Thread.currentThread();
        if (holder.get() == current) {
            holdCount--;
            if (holdCount == 0)
                holder.compareAndSet(current, null);
            return;
        }
        throw new RuntimeException("Not lock holder");
    }
}

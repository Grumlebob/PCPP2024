// For week 5
// sestoft@itu.dk * 2014-11-14
package lecture05;

import java.util.concurrent.atomic.AtomicReference;

// Non-reentrant lock supporting only tryLock and unlock.  In all
// cases the owner of the lock is the thread that equals holder.get().

public class SimpleTryLock {
    // Refers to holding thread, null iff unheld
    private final AtomicReference<Thread> holder = new AtomicReference<Thread>();

    public boolean tryLock() {
        final Thread current = Thread.currentThread();
        return holder.compareAndSet(null, current);
    }

    public void unlock() {
        final Thread current = Thread.currentThread();
        if (!holder.compareAndSet(current, null))
            throw new RuntimeException("Not lock holder");
    }

    // This would work too, because holder can change (in tryLock) only
    // if null, and current is never null.  But the above version is
    // better because of the symmetry with lock() and because it makes
    // it evident that holder only ever gets changed from null to
    // current, or back, where current is the current thread's object.
    public void unlockVariant() {
        final Thread current = Thread.currentThread();
        if (holder.get() == current)
            holder.set(null);
        else
            throw new RuntimeException("Not lock holder");
    }
}


// For week 5
package lecture05;

// Model implementation of an AtomicInteger

// Implementation of CAS using a lock for describing its meaning, and
// implementation of AtomicInteger style operations in terms of CAS.

class MyAtomicInteger {
    private int value;    // Visibility ensured by locking

    // Model implementation of compareAndSwap to illustrate its meaning.
    // In practice, compareAndSwap is not implemented using locks; it is
    // compiled to a machine instruction such as CMPXCHG
    public synchronized int compareAndSwap(int oldValue, int newValue) {
        int valueInRegister = this.value;
        if (this.value == oldValue)
            this.value = newValue;
        return valueInRegister;
    }

    public synchronized boolean compareAndSet(int oldValue, int newValue) {
        return oldValue == this.compareAndSwap(oldValue,newValue);
    }

    public synchronized int get() {
        return this.value;
    }

    public int addAndGet(int delta) {
        int oldValue, newValue;
        do {
            oldValue = get();
            newValue = oldValue + delta;
        } while (!compareAndSet(oldValue, newValue));
        return newValue;
    }

    public int getAndAdd(int delta) {
        int oldValue, newValue;
        do {
            oldValue = get();
            newValue = oldValue + delta;
        } while (!compareAndSet(oldValue, newValue));
        return oldValue;
    }

    public int incrementAndGet() {
        return addAndGet(1);
    }

    public int decrementAndGet() {
        return addAndGet(-1);
    }

    public int getAndSet(int newValue) {
        int oldValue;
        do {
            oldValue = get();
        } while (!compareAndSet(oldValue, newValue));
        return oldValue;
    }
}

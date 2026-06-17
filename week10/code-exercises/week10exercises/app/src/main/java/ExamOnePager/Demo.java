package ExamOnePager;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo {

    /*
    * Week 1:
    *
    * Week 2:
    *
    * Week 3:
    *
    * Week 4:
    *
    * Week 5:
    *
    * Week 6:
    *
    * Week 7:
    *
    * Week 8:
    *
    * Week 9:
    *
    * Week 10:
    *
    * Week 11:
    *
    * Week 12:
    *
    * Week 13:
    *
    * Week 14:
    * */

    //----LOCKING-----
    // Synchronized /Intrinsic lock / ReentrantLock
    // Visibility,
    // Reordering,
    // mutual exclusion (Critical zone) --
    public int counter = 0;
    public synchronized void incrementSync() {
        counter++;
    }

    //----VOLATILE / MEMORY BARRIER----
    // Visibility, reordering
    public volatile int counter_volatile = 0;
    public void incrementVolatile() {
        counter_volatile++;
    }
    //Imagine some Thread 2, that reads the value of counter_volatile
    //Then spins in a loop.
    //If counter_volatile is not volatile, the value can be cached and never updated.
    //If counter_volatile is volatile, the value is always read from the main memory.


    //---PUBLISHING & ESCAPING----
    // Making an object (safely) available to other threads.
    public static int blatantlyPublicated = 1;
    private Demo EscapingDemo = new Demo();
    public Demo escape() {
        return EscapingDemo;
    }

    public class SafePublication {
        private final int value = 42; //if never changed
        private volatile int value_volatile; //flush cache
        public AtomicInteger value_atomic = new AtomicInteger(0); //atomic
        public int value_is_default = 0; //default init

        public SafePublication() {
            // Safe publication
            synchronized (this) {
                value_is_default = 42;
            }
            // 1. Initializing an object reference from a static initializer;
            // 2. Storing a reference to it into a volatile field or AtomicReference;
            // 3. Storing a reference to it into a final field of a properly constructed object;
            // 4. Storing a reference to it into a field that is properly guarded by a lock.
        }
    }

    //--Thread confinement--
    //BAD
    public static int globalCounter = 0;
    //GOOD
    public class ObjectUsedBySingleThread {
        private int counter = 0;
    }

    //--Monitor pattern--
    // Encapsulation. State and methods to operate on that state.
    // Monitors = State + Semaphores + Conditions (queue for sleeping threads)
    // Lock queue = lock.lock()
    // Condition queue = condition.await()
    // Wait / Notify / NotifyAll / interrupt
    // An object intended to be used safely by more than one thread.
    // But only one process at a time is active within the monitor.

    // thread-safety / Correctness
    //• Race conditions
    //• Data races
    //• Visibility
    // Safety (nothing bad happens)
    // Liveness (something good eventually happens)
    // Fairness (no thread is starved)
    // Atomicity (group of statements that appear to execute as one)
    // Linearizability
    // Deadlock (two threads waiting for each other)
    // Happens-before reasoning
    // Immutability
    // Compare and Swap (CAS) algorithms

    //---Performance (Benchmarking)---


    //----SYNCHRONIZERS (Latch, Barrier, Semaphore)----

    //--- CAS  & Atomic Variables----------
    // "Optimistic locking"

    //--- Synchonized collections---

    //----THREAD POOLS----

    //----FORK/JOIN----

    //----COMPLETABLE FUTURE----

    //----STREAMS / RX-Java----
    // Source
    // Intermediate operations
    // Terminal operations

    //----PARALLEL STREAMS----
    // ForkJoin Pool - Work stealing




    public static void main(String[] args) {
        Demo demo = new Demo();

    }




}

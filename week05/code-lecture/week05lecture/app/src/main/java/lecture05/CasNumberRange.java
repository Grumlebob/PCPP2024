// For week 5
// raup@itu.dk * 2023-10-07

package lecture05;


import java.util.concurrent.atomic.AtomicReference;


// Class CasNumberRange from Goetz, page 326

public class CasNumberRange {

    // State of the class
    // Initialized to (0,0)
    private final AtomicReference<IntPair> values =
        new AtomicReference<IntPair>(new IntPair(0,0));


    public static void main(String[] args) {
        CasNumberRange cnr = new CasNumberRange();
        Thread t1 = new Thread(() -> {
                cnr.setUpper(5);
        });
        Thread t2 = new Thread(() -> {
                cnr.setLower(3);
        });
        t1.start();t2.start();
        try { t1.join();t2.join(); } catch (InterruptedException e) { System.out.println(e); }

        // It is possible that the concurrent execution of t1 and t2 produces an exception
        // Thus, the print below may produce (0,5) and (3,5)
        System.out.println("("+cnr.getLower() + ", " + cnr.getUpper() + ")");
    }


    public int getLower() { return values.get().lower;}
    public int getUpper() { return values.get().upper;}

    public void setLower(int i) {
        while(true) {
            IntPair oldv = values.get();
            if (i > oldv.upper) {
                throw new IllegalArgumentException("Can't set lower to " + i +" > upper");
            }
            IntPair newv = new IntPair(i, oldv.upper);
            if(values.compareAndSet(oldv,newv))
                return;
        }
    }

    public void setUpper(int i) {
        while (true) {
            IntPair oldv = values.get();
            if (i < oldv.lower) {
                throw new IllegalArgumentException("Can't set upper to " + i +" < lower");
            }
            IntPair newv = new IntPair(oldv.lower,i);
            if(values.compareAndSet(oldv,newv))
                return;
        }
    }

    // It is important that this class is immutable
    // because it will be used with an AtomicReference
    private static class IntPair {
        private final int lower;
        private final int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }
}

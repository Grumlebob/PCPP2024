// For week 3
// raup@itu.dk * 19/09/2021
package lecture03;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;


class BarrierExample {

    int parties        = 10;
    CyclicBarrier cb   = new CyclicBarrier(parties);
    int[] shared_array = new int[parties];

    public BarrierExample() {
        // Parallel initialization of the `shared_array`
        // We assign one position of the array to a different thread (variable independence)
        for (int i = 0; i < parties; i++) {
            new SetterClass(i).start();
        }
    }

    public static void main(String[] args) {
        new BarrierExample();
    }

    public class SetterClass extends Thread {

        int index;

        public SetterClass(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            shared_array[index] = index+1;
            // Try running the program commenting out this line
            try { cb.await(); } catch (InterruptedException|BrokenBarrierException e) { e.printStackTrace(); }
            // After this point the array is initialized and it is safe to read it (if you do not comment out the line above)
            System.out.println("Thread index " + index +
                               " is printing, shared_array[" + (index+1)%parties +"]="+shared_array[(index+1)%parties]);
        }
    }
}

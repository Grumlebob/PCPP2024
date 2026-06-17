// For week 4
// raup@itu.dk * 2023-09-16

// This class is a synthetic example to verify with JavaPathFinder

public class CounterAct {

    static final int N = 10;
    static int counter = 0;

    public static void main(String args[]) {
        Thread t1 = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < N; i++) {
                        counter++;
                    }
                }
            };

        Thread t2 = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < N; i++) {
                        counter++;
                    }
                }
            };

        t1.start();t2.start();
        try { t1.join();t2.join(); } catch (InterruptedException e) { }

        assert (counter > 2);
    }
}

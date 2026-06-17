// For week 2
// raup@itu.dk * 02/09/2021 (adapted from JLS documentation)
package lecture02;

public class JLSReorderingExample {

    public JLSReorderingExample() throws InterruptedException {
        for (int i = 0; i < 3_000_000; i++) {
            Test t = new Test();

            // Threads definition
            Thread one = new Thread(() -> {
                    t.one();
            });
            Thread other = new Thread(() -> {
                    t.two();
            });
            one.start();other.start();
            one.join();other.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new JLSReorderingExample();
    }
}

class Test {
    static int i = 0, j = 0;
    static void one() { i++; j++; }
    static void two() {
        if(j > i)
            System.out.println("j>i");
        // System.out.println("i=" + i + " j=" + j);
    }
}

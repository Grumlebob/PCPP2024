// For week 1
// raup@itu.dk * 2021-08-27 (from Goetz)
package lecture02;

public class NoVisibility {

    // Shared variables
    boolean ready;
    int number;

    public NoVisibility() {
        new ReaderThread().start();
        number = 42;
        ready  = true;
    }

    public static void main(String[] args) {
        new NoVisibility();
    }

    // Definition of the thread computation
    public class  ReaderThread extends Thread {
        public void run() {
            while(!ready) {
                Thread.yield();
            }
            System.out.println(number);
        }
    }
}

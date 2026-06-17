// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;

public class PrinterExperiment {

    public PrinterExperiment() {

        Printer p = new Printer();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                p.print();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                p.print();
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }

    }

    public static void main(String[] args) {
        new PrinterExperiment();
    }

    class Printer {
        public synchronized void print() {
            System.out.print("-");
            try { Thread.sleep(50); } catch (InterruptedException exn) { }
            System.out.print("|");
        }
    }
}



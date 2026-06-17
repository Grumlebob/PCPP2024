// For week 2
// raup@itu.dk * 02/09/2021
// raup@itu.dk * 08/09/2022

package lecture02;

public class NoVisibility1 {
    boolean running = true;

    public NoVisibility1() {
        Thread t1 = new Thread(() -> {
                while (running) {
                    /* do nothing */
                }
                System.out.println("t1 finishing execution");
        });
        t1.start();
        try{Thread.sleep(500);}catch(InterruptedException e){e.printStackTrace();}
        running = false;
        System.out.println("Main finishing execution");
    }

    public static void main(String[] args) {
        new NoVisibility1();
    }
}

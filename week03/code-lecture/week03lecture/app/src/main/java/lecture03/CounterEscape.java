// Week 3
// raup@itu * 2021/09/16
package lecture03;

class CounterEscape {

    public CounterEscape() throws InterruptedException {
        Counter c = new Counter();
        Thread t1 = new Thread(() -> {
                c.inc();
        });
        Thread t2 = new Thread(() -> {
                c.i++; // escaped the lock in inc()
        });
        t1.start();t2.start();
        t1.join();t2.join();
        int x=c.get();
        x+=23;
        System.out.println(c.get() + " | " + x);
    }

    public static void main(String[] args) throws InterruptedException {
        new CounterEscape();
    }
}

class Counter {
    /*private*/ int i=0;
    public synchronized void inc() {  i++; }
    public synchronized int get() { return i; }
}

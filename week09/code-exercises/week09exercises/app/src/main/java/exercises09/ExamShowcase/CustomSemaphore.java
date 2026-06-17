package exercises09.ExamShowcase;

public class CustomSemaphore {

    private volatile int allowedThreads;

    public CustomSemaphore(int allowedThreads) {
        this.allowedThreads = allowedThreads;
    }

    public synchronized void acquire() throws InterruptedException {
        //If no more threads are allowed,
        //We wait (which blocks the thread, until notified)
        while (allowedThreads == 0) {
            wait(); //release the synchronized lock, and wait for notify
        }
        allowedThreads--;
    }

    public synchronized void release() {
        allowedThreads++;
        notifyAll();
    }
}

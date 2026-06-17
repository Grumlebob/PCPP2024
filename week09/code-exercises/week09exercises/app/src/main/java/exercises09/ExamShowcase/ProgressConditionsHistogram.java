package exercises09.ExamShowcase;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgressConditionsHistogram {

    private final AtomicInteger[] bins;

    public ProgressConditionsHistogram(int span) {
        this.bins = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            bins[i] = new AtomicInteger(0);
        }
    }


    // If thread is running "Obstruced" (completely alone) It succeeds.
    // If there are conflicting threads, we do not guarantee progress.
    public void incrementObstructionFree(int bin) {
        AtomicInteger currentBin = bins[bin];
        int oldVal = currentBin.get();
        int newVal = oldVal + 1;
        currentBin.compareAndSet(oldVal, newVal);
    }


    //Lock-free - not starvation free
    public void incrementLockFree(int bin) {
        AtomicInteger currentBin = bins[bin];
        int oldValue, newValue;
        do {
            oldValue = currentBin.get();
            newValue = oldValue + 1;

        } while (!currentBin.compareAndSet(oldValue, newValue));
    }


    //Wait free (not technically correct, used only for demonstration)
    //Starvation-free
    //Bounded number of CAS retries.
    //Either progesses or helps other thread progress.
    public void incrementWaitFree(int bin) {
        final int MAX_RETRIES = 100; // Arbitrary bound for demonstration
        AtomicInteger currentBin = bins[bin];
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            int oldVal = currentBin.get();
            int newVal = oldVal + 1;
            if (currentBin.compareAndSet(oldVal, newVal)) {
                return; // success => done
            }
        }
        // If we truly can't succeed within MAX_RETRIES, we must do something else:
        // e.g., store the increment in a local buffer, or "help" mechanism (not shown).
        throw new RuntimeException("Wait-free operation timed out after " + MAX_RETRIES + " attempts.");
    }

    public int getCount(int bin) {
        return bins[bin].get();
    }

    public int getSpan() {
        return bins.length;
    }
}

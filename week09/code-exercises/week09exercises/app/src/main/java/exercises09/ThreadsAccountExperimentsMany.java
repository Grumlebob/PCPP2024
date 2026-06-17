// jst@itu.dk * 04/10/2024
package exercises09;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ThreadsAccountExperimentsMany {

  static final int N = 10;
  static final int NO_TRANSACTION = 5;
  static final int NO_THREADS = 10;
  static final Account[] accounts = new Account[N];
  static final Random rnd = new Random();

  public static void main(String[] args) {
    new ThreadsAccountExperimentsMany();
  }

  public ThreadsAccountExperimentsMany() {
    // Initialize accounts
    for (int i = 0; i < N; i++) {
      accounts[i] = new Account(i);
    }

    // Create a ForkJoinPool with the specified number of threads
    ExecutorService executor = new ForkJoinPool(NO_THREADS);

    // Submit tasks to the executor
    for (int i = 0; i < NO_THREADS; i++) {
      executor.submit(() -> doNTransactions(NO_TRANSACTION));
    }

    // Prevents executor from accepting new tasks
    executor.shutdown(); //This is not needed
    try {
      if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        System.out.println("Executor did not terminate in the specified time.");
        executor.shutdownNow();
      } else {
        System.out.println("All transactions completed, and executor shut down.");
      }
    } catch (InterruptedException e) {
      System.err.println("Termination interrupted: " + e.getMessage());
      executor.shutdownNow();
    }
  }

  private static void doNTransactions(int noTransactions) {
    for (int i = 0; i < noTransactions; i++) {
      long amount = rnd.nextInt(5000) + 100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N - 2) + 1) % N; // Ensure target != source
      doTransaction(new Transaction(amount, accounts[source], accounts[target]));
    }
  }

  private static void doTransaction(Transaction t) {
    //for 9.1.3
    //System.out.println("Executing transaction: " + t); // Print to confirm executor activity
    t.transfer();
  }

  static class Transaction {
    final Account source, target;
    final long amount;

    Transaction(long amount, Account source, Account target) {
      this.amount = amount;
      this.source = source;
      this.target = target;
    }

    public void transfer() {
      Account min = accounts[Math.min(source.id, target.id)];
      Account max = accounts[Math.max(source.id, target.id)];
      synchronized (min) {
        synchronized (max) {
          source.withdraw(amount);
          try { Thread.sleep(50); } catch (Exception e) {} // Simulate transaction time
          target.deposit(amount);
        }
      }
    }

    public String toString() {
      return "Transfer " + amount + " from " + source.id + " to " + target.id;
    }
  }

  static class Account {
    public final int id;
    private long balance = 0;

    Account(int id) { this.id = id; }

    public void deposit(long sum) { balance += sum; }
    public void withdraw(long sum) { balance -= sum; }
    public long getBalance() { return balance; }
  }
}

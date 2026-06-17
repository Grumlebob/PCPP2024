package lecture08;

class PrimeCounter {

  private int count= 0;
  public synchronized void increment() {
    count= count + 1;
  }
  public synchronized int get() { 
    return count; 
  }
  public synchronized void setZero() {
    count= 0;
  }

  /*
  private AtomicLong count = new AtomicLong(); //Initialized to 0
  public void increment() {
    count.getAndIncrement();
  }
  public long get() { 
    return count.get(); 
  }

  public void setZero() {
    count.set(0);
  }
*/

}
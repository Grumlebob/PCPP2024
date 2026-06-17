package lecture09;
import benchmarking.SearchAndSort;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
//Used in Quicksort implemented with Executors

class QuickSortTask implements Runnable {
	public int[] arr;
	public int low, high, threshold;
  public ExecutorService pool;

	public QuickSortTask(int[] arr, int low, int high, ExecutorService pool, int threshold){
		this.arr= arr; this.low= low; this.high= high; this.pool= pool; this.threshold= threshold;
	}

  public int size(){return (high-low); }

  @Override
  public void run() {
    // modified Quicksort using a Executor tasks 

    int a= low;
    int b= high;
    Future<?> lowTask= null;
    Future<?> highTask= null;

    if (a < b) {
      int i = a, j = b;
      int x = arr[(i+j) / 2];                
      do {                                   
        while (arr[i] < x) i++;              
        while (arr[j] > x) j--; 
        if (i <= j) {
          swap(arr, i, j);
          i++; j--;
        }                                    
      } while (i <= j);

      if ((j-a)>= threshold)
        lowTask= pool.submit(new QuickSortTask(arr, a, j, pool, threshold));
      else // all remaining work done without starting more tasks
        SearchAndSort.qsort(arr, a, j);

      if ((b-i)>= threshold)
        highTask= pool.submit(new QuickSortTask(arr, i, b, pool, threshold)); 
      else // all remaining work done without starting more tasks
        SearchAndSort.qsort(arr, i, b);
    }
  
    //Waiting for longest running subtask to finish
    try {
      if (lowTask != null ) lowTask.get();
      if (highTask != null) highTask.get();
    } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
  }

  private static void swap(int[] arr, int s, int t) {
    int tmp = arr[s];  arr[s] = arr[t];  arr[t] = tmp;
  }
}

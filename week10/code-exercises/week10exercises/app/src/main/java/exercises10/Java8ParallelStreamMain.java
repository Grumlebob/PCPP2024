package exercises10;

import java.util.Arrays;
import java.util.stream.IntStream;
public class Java8ParallelStreamMain {
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("Using Sequential Stream");
        System.out.println("=================================");
        int[] array= {1,2,3,4,5,6,7,8,9,10};
        //10.3.2
        int[] largeArray = IntStream.rangeClosed(1, 1000).toArray();
        //array = largeArray;
        IntStream intArrStream=Arrays.stream(array);
        intArrStream.forEach(s->
                {
                    System.out.println(s+" "+Thread.currentThread().getName());
                }
        );
        System.out.println("=================================");
        System.out.println("Using Parallel Stream");
        System.out.println("=================================");
        IntStream intParallelStream=Arrays.stream(array).parallel();
        intParallelStream.forEach(s->
                {
                    System.out.println(s+" "+Thread.currentThread().getName());
                }
        );

        //10.3.3
        System.out.println("=================================");
        System.out.println("Using Sequential Stream with Prime Check");
        System.out.println("=================================");
        Arrays.stream(array).forEach(s -> {
            if (isPrime(s)) {
                System.out.println(s + " is prime - " + Thread.currentThread().getName());
            }
        });

        System.out.println("=================================");
        System.out.println("Using Parallel Stream with Prime Check");
        System.out.println("=================================");
        Arrays.stream(array).parallel().forEach(s -> {
            if (isPrime(s)) {
                System.out.println(s + " is prime - " + Thread.currentThread().getName());
            }
        });
    }

    private static boolean isPrime(int n) {
        int k = 2;
        while (k * k <= n && n % k != 0)
            k++;
        return n >= 2 && k * k > n;
    }
}

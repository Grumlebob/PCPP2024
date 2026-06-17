// Week 3
// raup@itu.dk * 12/09/2021
// raup@itu.dk * 15/09/2022

package lecture03;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class SynchronizedSetProblem {

    public SynchronizedSetProblem() throws InterruptedException {

        for (int i = 0; i < 1_000_000; i++) {

            List<Integer> l = new ArrayList<Integer>();
            List<Integer> lSync = Collections.synchronizedList(l); // a program only accessing a thread-safe class does NOT imply that the program is thread-safe

            Thread t1 = new Thread(() -> { add1IfAbsent(lSync); });
            Thread t2 = new Thread(() -> { add1IfAbsent(lSync); });

            t1.start();t2.start();
            t1.join();t2.join();

            if (!lSync.contains(2))
                System.out.println("2 was not inserted");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new SynchronizedSetProblem();
    }

    private void add1IfAbsent(List<Integer> l) {
        if (!l.contains(1))
            l.add(1);
        else
            l.add(2);
    }

}

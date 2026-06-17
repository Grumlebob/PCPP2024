// Week 3
// raup@itu * 2024/09/08
package lecture03;

import java.util.List;
import java.util.ArrayList;

class AccessNoState {
    public AccessNoState() throws InterruptedException {
        for (int i = 0; i < 1_000; i++) {

            final ArrayList<Integer> l = new ArrayList<Integer>();
            final C c = new C();

            Thread t1 = new Thread(() -> {
                    l.add(1);
            });
            Thread t2 = new Thread(() -> {
                    c.n(l);
            });

            t1.start();t2.start();
            t1.join();t2.join();

            // The lack of synchronization will cause data races on
            // the object `l`. However, note that class C is
            // thread-safe, as object `l` is not part of the class
            // state.

            // A symptom of this problem is that we will observe
            // objects `l` whose size is less than 2.
            if (l.size() < 2)
                System.out.println(l);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new AccessNoState();
    }
}

class C {
    // class state
    private int i = 0;

    public synchronized void n(List<Integer> l) {
        // the method modifies the object passed as parameter, this
        // object may be used by other threads in any way. As a
        // consequence, the implicit lock in this method is not
        // sufficient to ensure mutual exclusion for accessing this
        // object; and more generally it is not sufficient to ensure
        // that there exist a happens-before relation between reads an
        // writes on the object.

        // Nevertheless, as mentioned above, class C remains
        // thread-safe; as the definition of thread-safety only
        // required to protect the class state. The class state for
        // this class only contains varaible `i`.
        l.add(42);
    }
}

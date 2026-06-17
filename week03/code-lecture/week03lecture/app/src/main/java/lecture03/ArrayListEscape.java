// Week 3
// raup@itu.dk * 2021/09/16
package lecture03;

import java.util.List;
import java.util.ArrayList;

class ArrayListEscape {

    public ArrayListEscape() {
        IntArrayList array = new IntArrayList();
        new Thread(() -> {
                array.set(0,1); // access shared state with lock
        }).start();
        new Thread(() -> {
                array.get().set(0,42); // access shared state without locks
        }).start();
    }

    public static void main(String[] args) {
        new ArrayListEscape();
    }
}

class IntArrayList {
    private final List<Integer> a = new ArrayList<Integer>();
    public IntArrayList() { a.add(1); }
    public synchronized void set(Integer index, Integer elem) {  a.set(index,elem); }
    public synchronized List<Integer> get() { return a; }
}

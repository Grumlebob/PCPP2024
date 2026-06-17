// Week 3
// raup@itu.dk * 19/09/2021
package lecture03;

import java.util.Objects;

class TestUnsafeInitialization {
    public TestUnsafeInitialization() {
        int N = 10_000_000;
        for (int i = 0; i < N; i++) {

            UnsafeInitialization u = new UnsafeInitialization();

            new Thread(() -> {
                    if (u.readX()!=42)
                        System.out.println("x is not equal 42");
            }).start();

            new Thread(() -> {
                    if (Objects.isNull(u.readO()))
                        System.out.println("o is null");
            }).start();
        }
    }

    public static void main(String[] args) {
        new TestUnsafeInitialization();
    }
}


class UnsafeInitialization {
    private int x;
    private Object o;

    public UnsafeInitialization() {
        this.x = 42;
        this.o = new Object();
    }

    public int readX() {
        return this.x;
    }

    public Object readO() {
        return this.o;
    }
}

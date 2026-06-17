package exercises03;

import java.util.concurrent.Semaphore;

public class CustomBlockingQueue<T> implements BoundedBufferInteface<T> {

    //Shared lock to protect the buffer
    private final Semaphore sharedLock;
    private final T[] buffer;

    //Semaphore's behind the scene implementation is a counter.
    //We init the counter to 0, because the queue is initially empty
    private final Semaphore takeSpaces;
    private int takeIndex;

    //Same as above, but instead we init the counter to the capacity of the queue
    //Because initially we can insert n elements
    private final Semaphore insertSpaces;
    private int insertIndex;


    public CustomBlockingQueue(int capacity) {
        buffer = (T[]) new Object[capacity];
        sharedLock = new Semaphore(1);
        takeSpaces = new Semaphore(0);
        insertSpaces = new Semaphore(capacity);
    }


    @Override
    public T take() throws Exception {
        //1. we want to aquire the lock for takeSpaces.
        //If takeSpaces counter is 0, it will block thread.
        //It is important we aquire this lock before the shared lock, to prevent deadlock
        takeSpaces.acquire();
        //2. If we are unblocked, we can now aquire the shared lock to protect the buffer
        sharedLock.acquire();
        try {
            //3. We simply get the element and increment our circular counter
            T elem = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % buffer.length;
            return elem;
        } finally {
            //4. We release the shared lock, so other threads can access the buffer
            sharedLock.release();
            //5. Importantly! we release the INSERT lock.
            //This basically means we increment the space counter by 1,
            //so another thread now knows there is an empty space, in which it can insert.
            //So if another thread is blocked, it will now be released to insert an element.
            //We do this the opposite way in insert.
            //So insert will increment the TAKE counter, as to indicate to another thread
            //that a new element is available to take.
            insertSpaces.release();
        }
    }

    @Override
    public void insert(T elem) throws Exception {
        //See explanation in take() method - They are reverse of each other
        insertSpaces.acquire();

        sharedLock.acquire();

        try {
            buffer[insertIndex] = elem;
            insertIndex = (insertIndex + 1) % buffer.length;
        } finally {
            sharedLock.release();
            takeSpaces.release();
        }

    }
}

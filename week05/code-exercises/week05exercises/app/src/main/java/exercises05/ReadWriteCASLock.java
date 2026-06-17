package exercises05;

import java.util.concurrent.atomic.AtomicReference;

class ReadWriteCASLock implements SimpleRWTryLockInterface {

    private final AtomicReference<Holders> holders = new AtomicReference<>(null);

    /*
    * Method readerTryLock is called by a thread that tries to obtain a read lock.
    * It must succeed and return true if
the lock is held only by readers (or nobody),
* and return false if the lock is held by a writer.*/
    @Override
    public boolean readerTryLock() {
        Holders current;
        do {
            current = holders.get();
            //!! check for null (current == null)
            //with a RW lock, a reader can't read if there is a writer
            if (current instanceof Writer) return false;
            //if there is no writer, we can add the reader
            //ReadersList is basically a "allowed readers list"
            if (((ReaderList) current).contains(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Reader already holds the lock");
            }

        } while (!holders.compareAndSet(current, new ReaderList(Thread.currentThread(), (ReaderList) current)));
        //if we reach this point, we have successfully added the reader
        return true;
    }

    /*
    * Method readerUnlock is called to release a read lock,
    * and must throw an exception if the calling thread does
not hold a read lock.*/
    @Override
    public void readerUnlock() {
        Holders current;
        ReaderList newList;
        do {
            current = holders.get();
            // i-iii) If we are a writer, or not in the readerslist:
            if (!(current instanceof ReaderList) || !((ReaderList) current).contains(Thread.currentThread())) {
                throw new IllegalMonitorStateException("Thread does not hold a read lock");
            }
            //If we make it here. We know we are a reader on the readerslist
            newList = ((ReaderList) current).remove(Thread.currentThread());  // Update newList
            //If there are more readers, update holder to the new list
        } while (!holders.compareAndSet(current, newList));
    }


    /*
    * Method writerTryLock is called by a thread that tries to obtain a write lock.
    *  It must succeed and return true if
the lock is not already held by any thread,
* and return false if the lock is held by at least one reader or by a writer.*/
    @Override
    public boolean writerTryLock() {
        return holders.compareAndSet(null, new Writer(Thread.currentThread()));
    }

    /*
    * Method writerUnlock is called to release the write lock,
    *  and must throw an exception if the calling thread
does not hold a write lock.*/
    @Override
    public void writerUnlock() {
        Holders current = holders.get();
        // Check if the current thread holds the write lock
        //First we check if it is a writer, otherwise we short circuit and throw the exception
        //Then we check if the thread is the same as the current thread
        if (!(current instanceof Writer) || ((Writer) current).thread != Thread.currentThread()) {
            throw new IllegalMonitorStateException("Thread does not hold the write lock");
        }
        //If above if statement doesn't hold, then we are
        //sure that we are the current writer
        //we release the lock, by setting the holder to null
        holders.compareAndSet(current, null);
    }

    private static abstract class Holders {
    }

    // Immutable linked list of ReaderList to hold multiple readers
    private static class ReaderList extends Holders {
        private final Thread thread;
        private final ReaderList next;

        public ReaderList(Thread thread, ReaderList next) {
            this.thread = thread;
            this.next = next;
        }

        //Traverses the linkedlist, and checks if the thread is in the list
        public boolean contains(Thread t) {
            for (ReaderList current = this; current != null; current = current.next) {
                if (current.thread == t) return true;
            }
            return false;
        }

        public ReaderList remove(Thread t) {
            if (this.thread == t) {
                return this.next;  // Remove the current thread
            } else if (this.next != null) {
                return new ReaderList(this.thread, this.next.remove(t));  // Recurse to find the thread
            } else {
                return this;  // Thread not found, return the unchanged list
            }
        }
    }

    private static class Writer extends Holders {
        public final Thread thread;

        public Writer(Thread thread) {
            this.thread = thread;
        }
    }
}

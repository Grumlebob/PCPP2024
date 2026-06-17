// For week 5
// sestoft@itu.dk * 2014-11-16

// Unbounded list-based lock-free queue by Michael and Scott 1996 (who
// calls it non-blocking).
package lecture05;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

// --------------------------------------------------
// Lock-free queue, using CAS and reflection on field Node.next

class MSQueueRefl<T> implements UnboundedQueue<T> {
    private final AtomicReference<Node<T>> head, tail;

    public MSQueueRefl() {
        // Essential to NOT make dummy a field as in Goetz p. 334, that
        // would cause a memory management disaster, huge space leak:
        Node<T> dummy = new Node<T>(null, null);
        head = new AtomicReference<Node<T>>(dummy);
        tail = new AtomicReference<Node<T>>(dummy);
    }

    @SuppressWarnings("unchecked")
    // Java's @$#@?!! generics type system: abominable unsafe double type cast
    private final AtomicReferenceFieldUpdater<Node<T>, Node<T>> nextUpdater
        = AtomicReferenceFieldUpdater.newUpdater((Class<Node<T>>)(Class<?>)(Node.class), 
                                                 (Class<Node<T>>)(Class<?>)(Node.class), 
                                                 "next");

    public void enqueue(T item) { // at tail
        Node<T> node = new Node<T>(item, null);
        while (true) {
            Node<T> last = tail.get(), next = last.next;
            if (last == tail.get()) {         // E7
                if (next == null)  {
                    // In quiescent state, try inserting new node
                    if (nextUpdater.compareAndSet(last, next, node)) {
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    // Queue in intermediate state, advance tail
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    public T dequeue() { // from head
        while (true) {
            Node<T> first = head.get(), last = tail.get(), next = first.next;
            if (first == head.get()) {        // D5
                if (first == last) {
                    if (next == null)
                        return null;
                    else
                        tail.compareAndSet(last, next);
                } else {
                    T result = next.item;
                    if (head.compareAndSet(first, next)) {
                        return result;
                    }
                }
            }
        }
    }

    private static class Node<T> {
        final T item;
        volatile Node<T> next;

        public Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }
    }
}

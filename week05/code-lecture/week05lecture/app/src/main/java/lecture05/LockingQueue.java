// For week 5
// sestoft@itu.dk * 2014-11-16
package lecture05;

// ------------------------------------------------------------
// Unbounded lock-based queue with sentinel (dummy) node

public class LockingQueue<T> implements UnboundedQueue<T> {
    // Invariants:
    // The node referred by tail is reachable from head.
    // If non-empty then head != tail,
    //    and tail points to last item, and head.next to first item.
    // If empty then head == tail.

    private static class Node<T> {
        final T item;
        Node<T> next;

        public Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }
    }

    private Node<T> head, tail;

    public LockingQueue() {
        head = tail = new Node<T>(null, null);
    }

    public synchronized void enqueue(T item) { // at tail
        Node<T> node = new Node<T>(item, null);
        tail.next = node;
        tail = node;
    }

    public synchronized T dequeue() {     // from head
        if (head.next == null)
            return null;
        Node<T> first = head;
        head = first.next;
        return head.item;
    }
}

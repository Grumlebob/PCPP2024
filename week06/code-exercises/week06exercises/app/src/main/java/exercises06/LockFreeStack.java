// For week 6
// raup@itu.dk * 2023-10-20
package exercises06;

import java.util.concurrent.atomic.AtomicReference;

// Treiber's LockFree Stack (Goetz 15.4 & Herlihy 11.2)
class LockFreeStack<T> {
    AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(); // Initializes to null

    //----Specification of push(v)-----
    //Pre condition: The state is some stack of elements s
    //Post condition: v concatenated with s, where v is top node.
    public void push(T value) {
        Node<T> newHead = new Node<T>(value);
        Node<T> oldHead;
        do {
            oldHead      = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead,newHead)); //H1: Linearization point
    }

    //----Specification of pop()-----
    //Case 1: Stack is empty
    //Pre condition: The state of the stack is empty
    //Post condition: return null

    //Case 2: Stack is not empty
    //Pre condition: The state of the stack is head concatenated with rest
    //Post condition: return head, and the state of the stack is rest
    public T pop() {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = top.get();    //P1: Linearization point
            if(oldHead == null) { return null; }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead,newHead)); //P2: Linearization point

        return oldHead.value;
    }






    // Internal class for nodes
    private static class Node<T> {
        public final T value;
        public Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next  = null;
        }
    }
}

// For week 5
// sestoft@itu.dk * 2014-11-16
package lecture05;

interface UnboundedQueue<T> {
    void enqueue(T item);
    T dequeue();
}

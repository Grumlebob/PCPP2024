package exercises06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadLocalRandom;

public class TestLockFreeStack {

    @Test
    public void testPushCorrectness() throws InterruptedException {
        int numThreads = 10000;

        //Known answer, of atomically added values.
        AtomicInteger totalSum = new AtomicInteger(0);

        //Our stack we are testing. Should match the totalSum.
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        //Our test runner from week 4/5
        ConcurrentTestRunner runner = new ConcurrentTestRunner(numThreads);

        runner.runConcurrently(() -> {

                //A thread-local random number generator
                int value = ThreadLocalRandom.current().nextInt(100);
                //Value is pushed onto our stack
                stack.push(value);
                //The known value is added to our accumulator, which should match
                //the stack after running.
                totalSum.addAndGet(value);

        });

        runner.shutdown();

        int stackSum = 0;
        Integer element;
        while ((element = stack.pop()) != null) {
            stackSum += element;
        }

        assertEquals(totalSum.get(), stackSum, "Sum of pushed elements should equal sum of popped elements");
    }

    @Test
    public void testPopCorrectness() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        int numElements = 10000;
        int totalSum = 0;

        //Initilize the stack with already pushed elements
        //As specificed by the assignment.
        for (int i = 0; i < numElements; i++) {
            int value = ThreadLocalRandom.current().nextInt(100);
            stack.push(value);
            totalSum += value;
        }

        int numThreads = numElements;

        //Our known answer, which should match the stack after running.
        AtomicInteger poppedSum = new AtomicInteger(0);

        //Our test runner from week 4/5
        ConcurrentTestRunner runner = new ConcurrentTestRunner(numThreads);

        runner.runConcurrently(() -> {
            Integer element;
            element = stack.pop();
            poppedSum.addAndGet(element);

        });

        runner.shutdown();

        //Known answer should match the pre-initialized stack.
        assertEquals(totalSum, poppedSum.get(), "Sum of pushed elements should equal sum of popped elements");
        //After poppping once more, we should get null, because it should be empty.
        assertNull(stack.pop(), "Stack should be empty after all elements are popped");
    }


}
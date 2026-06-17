package lecture05;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class TestAtomicInteger {

    MyAtomicInteger mai;

    @BeforeEach
    public void init() {
        mai = new MyAtomicInteger();
    }

    @Test
    @DisplayName("Sequential test for MyAtomicInteger class")
    public void testSequential() {
        assertTrue (mai.compareAndSwap(72,42) != 72 , "Swapping succeeded, it shouldn't have");
        assertTrue (mai.compareAndSwap(0,42)  == 0  , "Swapping failed, it should be 0");
        assertTrue (mai.get() == 42                 , "Get failed, it should be 42");
        assertTrue (mai.compareAndSet(42,72)        , "Set failed, and it shouldn't have");
        assertTrue (!mai.compareAndSet(42,0)        , "Set succeeded, and it shouldn't have");
        assertTrue (mai.get() == 72                 , "Get failed, it should be 42");
        assertTrue (mai.addAndGet(1) == 73          , "Addition failed, it should be 73");
        assertTrue (mai.getAndAdd(1) == 73          , "Get failed, it should be 79");
    }
}

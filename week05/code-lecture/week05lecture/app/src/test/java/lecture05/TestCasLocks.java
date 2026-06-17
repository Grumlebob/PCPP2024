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

public class TestCasLocks {

    @Test
    @DisplayName("Sequential test for CAS locks class")
    public void testSequential() {
        {
            final SimpleTryLock lock = new SimpleTryLock();
            assertTrue(lock.tryLock());
            assertTrue(!lock.tryLock());
            lock.unlock();
            try { lock.unlock();  assertTrue(false); } catch (RuntimeException exn) { }
        }
        {
            final ReentrantTryLock lock = new ReentrantTryLock();
            assertTrue(lock.tryLock());
            assertTrue(lock.tryLock());
            lock.unlock();
            lock.unlock();
            try { lock.unlock();  assertTrue(false); } catch (RuntimeException exn) { }
        }
        {
            final SimpleLock lock = new SimpleLock();
            lock.lock();
            // lock.lock(); // Should block
            lock.unlock();
            try { lock.unlock();  assertTrue(false); } catch (RuntimeException exn) { }
        }
        {
            final MyReentrantLock lock = new MyReentrantLock();
            lock.lock();
            lock.lock();
            lock.unlock();
            lock.unlock();
            try { lock.unlock();  assertTrue(false); } catch (RuntimeException exn) { }
        }
    }
}

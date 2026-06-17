// For week 4
// raup@itu.dk * 2024-09-16

package lecture04;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Description;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;
import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE_INTERESTING;
import static org.openjdk.jcstress.annotations.Expect.FORBIDDEN;

@JCStressTest
@Description("Testing race conditions on concurrent increments of different Counter Objects")

// Outcomes 

// Using the @Outcome decorator we can specify what are the acceptable
// and forbidden outcomes of the program

// This statement specifies that 2 is an acceptable outcome
@Outcome(id = "2", expect = ACCEPTABLE, desc = "Counter correctly incremented")
// This statement specifies that any value in the set {0,1} is
// forbidden (i.e., it is an error)
@Outcome(id = {"0","1"}, expect = FORBIDDEN, desc = "Race condition occurred")

@State()
public class CounterTestJCStress {

    // As for JUnit testing, we start by initializing the objects to
    // test or any auxiliary variables (note that this test uses no
    // auxiliary variables)

    private Counter cnt = new CounterDR();
    // private Counter cnt = new CounterSync();
    // private Counter cnt = new CounterAto();


    // The decorator @Actor specifies that this function must be
    // executed by an independent thread. We have two threads that
    // execute the method inc() from class Counter.
    @Actor
    public void actor1() {
        cnt.inc();
    }

    @Actor
    public void actor2() {
        cnt.inc();
    }

    // The decorator @Arbiter defines a thread that will be executed
    // after all @Actor threads terminate. It can be used to collect
    // the results of the program. The parameter r of type
    // I_Result---which has an attribute r1---is the object to which
    // the @Outcome entries refer. That is, when we write id = "2"
    // inside an @Outcome statement above, we mean that r.r1 == 2
    // after the test has finished.    
    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = cnt.get();
    }

}

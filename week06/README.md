# Lecture 6: Linearizability

## Goals

The goals of this lecture are:

* Explain and use the notion of linearizability.
* Compare linearizability with other notions of correctness for concurrent objects. 
* Design and use common lock-free data structures.

## Readings

* Herlihy:
  * Chapter 3, complete.

* Leslie Lamport. [How to Make a Multiprocessor Computer That Correctly Executes Multiprocess Programs](https://www.microsoft.com/en-us/research/uploads/prod/2016/12/How-to-Make-a-Multiprocessor-Computer-That-Correctly-Executes-Multiprocess-Programs.pdf). IEEE Transactions on Computers C-28. 1979.
  * This paper introduces sequential consistency. It complements the definition in Herlihy, Chapter 3.



### Optional readings

* Maurice P. Herlihy and Jeannette M. Wing. [Linearizability: A Correctness Condition for Concurrent Objects](https://dl.acm.org/doi/10.1145/78969.78972). ACM Transactions on Programming Languages and Systems. Volume 12. Issue 3. 1990.
  
* Maged M. Michael and Michael L. Scott. [Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue Algorithms](https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf). Symposium on Principles of Distributed Computing (PODC). 1996.

* Lindsay Groves. [Verifying Michael and Scott's Lock-Free Queue Algorithm using Trace Reduction](https://dl.acm.org/doi/pdf/10.5555/1379361.1379385). Symposium on Computing. Volume 77. 2008.

* Viktor Vafeiadis. [Automatically Proving Linearizability](https://link.springer.com/content/pdf/10.1007/978-3-642-14295-6_40.pdf?pdf=inline%20link). International Conference on Computer Aided Verification (CAV). 2010.

* David Chase and Yossi Lev. [Dynamic Circular Work-Stealing Deque](https://www.dre.vanderbilt.edu/~schmidt/PDF/work-stealing-dequeue.pdf). ACM Symposium on Parallelism in Algorithms and Architectures (SPAA). 2005.

* Jeremy Manson, William Pugh and Sarita V. Adve. [The Java Memory Model](https://dl-acm-org.ep.ituproxy.kb.dk/doi/abs/10.1145/1040305.1040336). In Proceedings of the 32nd ACM SIGPLAN-SIGACT symposium on Principles of programming languages (POPL'05). (part of the optional readings in week 2)

## Lecture slides

See file [lecture06.pdf](./lecture06.pdf).

### Exercises

See file [exercises06.pdf](./exercises06.pdf).

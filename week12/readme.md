# Lecture 12: Message Passing I

## Goals

The goals of this lecture are:

* Explain and reason about the Actor model of concurrent computation.
* Explain the differences between message passing and shared memory concurrency.
* Implement systems based on the Actor model using Erlang.

## Readings

* Gul A. Agha. [Actors: A Model Of Concurrent Computation In Distributed Systems](http://dspace.mit.edu/handle/1721.1/6952). MIT Press 1985:
  * Chapter 2, complete.
	* This book is a bit old, but this chapter provides an objective comparison of shared memory and the actor model.

* Learn You Some Erlang for Great Good!
  * [The Hitchhiker's Guide to Concurrency](https://learnyousomeerlang.com/the-hitchhikers-guide-to-concurrency)
  * [More On Multiprocessing](https://learnyousomeerlang.com/more-on-multiprocessing)
  

* Erlang official documentation (for version OTP 26) 
  * [Chapter 3 - Concurrent Programming](https://www.erlang.org/docs/26/getting_started/conc_prog)

### Optional readings

* Erlang Reference Manual (for version OTP 26). As mentioned last week, do not treat the manual as a textbook, but simply as a reference to use when you would like to get the details of specific parts of the Erlang language. The chapters below contain details for some of the concepts that we will cover in this lecture.
  * [Chapter 14 - Processes](https://www.erlang.org/docs/26/reference_manual/processes)

* Learn You Some Erlang for Great Good!
  * [Designing a Concurrent Application](https://learnyousomeerlang.com/designing-a-concurrent-application)
    * Ignore the parts about monitoring and supervision. Focus on the plain Erlang parts. The purpose of this reading to add another example of building an actor system in Erlang.

## Lecture slides

See file [lecture12.pdf](lecture12.pdf).

## Exercises

See file [exercises12.pdf](exercises12.pdf).

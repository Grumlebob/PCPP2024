# Lecture 8: Performance measurements

## Goals

The goals of this lecture are:

* Motivate the need for performance measurements
* Enable you to perform performance measurements of your own code
* Enable you to  benchmark a number of Java concepts including: object creation, threads and an algorithm for computing prime factors.
* Introduce the statistics of benchmarking (normal distribution, mean and variance).

## Readings 

* The note by Peter Sestoft: [Microbenchmarks in Java and C sharp](https://github.itu.dk/jst/PCPP2024-Public/blob/main/week08/benchmarkingNotes.pdf)
that can be found in the GitHub folder with course material for week 8.

You may skip sections 9-12.

## To do before lecture 8

* prepare an answer for the first blue question in this lecture: 
1: Assume that a single floating-point multiplication takes one time unit on your PC.
(Approximately) How many time units will it take to create and start a thread?

* make sure you have a local copy (on your computer) of  `code-exercises` (same folder as this file)
* test that you can run ` measurement.java ` (in this subdirectory ` week08/code-exercises/Week08exercises/app/src/main/java/exercises08 `)
for example by executing:

 ` gradle -PmainClass=exercises08.Measurement run `

### Optional readings
* The pitfalls of using floating point numbers: 

 * David Goldberg [What Every Computer Scientist Should Know About Floating-Point Arithmetic](https://github.itu.dk/jst/PCPP2023-Public/blob/main/week05/IEEE754_article.pdf)


## Lecture slides
Could be updated, so please check that you have the latest version

[lecture08.pdf](https://github.itu.dk/jst/PCPP2024-Public/blob/main/week08/lecture08.pdf)


## Exercises

[exercises08.pdf](https://github.itu.dk/jst/PCPP2024-Public/blob/main/week08/exercises08.pdf)

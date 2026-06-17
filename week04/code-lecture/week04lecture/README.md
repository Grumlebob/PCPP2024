# Code Lecture | Week 04

The structure of this week's Gradle project is a bit special because we are external libraries and plugins for [JUnit 5](https://junit.org/junit5/) and [JCStress](https://github.com/openjdk/jcstress/tree/master). This file contains a few clarifications regarding the content of this directory.

This directory contains a regular Gradle project. The only special detail is where different types of tests are located in the directory. It is common that the location of tests imposed by the testing library or framework.

## JUnit (part of course material)

* JUnit tests are located in the directory `src/test/java/lecture04/`. This is an important detail, as JUnit tests are the ones we will use throughout the course.

* To run JUnit tests with grade, execute `$ gradle cleanTest test --tests <package>.<test_class>` from the root of the project. For instance, `$ gradle cleanTest test --tests lecture04.CounterTest`.

## JCStress (optional material)

* Java Concurrency Stress (JCStress) is an (experimental) testing framework part of the OpenJDK. This Gradle project is configured with a JCStress plugin to run illustrate its usage.

* The example JCStress test is located in the directory `src/jcstress/java/lecture04/`.

* To run JCStress, execute `$ gradle jcstress --tests "CounterTestJCStress"` from the root of the project.

* If you liked JCStress, feel free to contact `raup@itu.dk` to work on a MSc thesis project with JCStress. An interesting line of research would be to investigate to which extent JCStress may be used to test thread-safety in Java classes.
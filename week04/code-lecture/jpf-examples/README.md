# JavaPathFinder examples (optional material)

This folder contains two illustrative examples that can be formally verified with [JavaPathFinder](https://github.com/javapathfinder/jpf-core). This material is optional for the course.

The main challenge in running this examples is to properly install JavaPathFinder. We recommend to follow the steps in this guide https://www.eecs.yorku.ca/course_archive/2020-21/W/4315/material/book.pdf. If JavaPathFinder is correctly installed in your system you should be able to execute the command `$ jpf`. If you would like to get help installing JavaPathFinder feel free to contact `raup@itu.dk`.

To run the examples, navigate to the desired directory, compile all java files with `javac *.java` and call `jpf` with the corresponding `.jpf` file as parameter. For instance, to run the "counter act" example, navigate to the directory `counter/`, execute `$javac *.java$` and, afterwards, `$ jpf CounterAct.jpf`.

If you are interested in learning about program verification, feel free to contact `raup@itu.dk`. We can discuss MSc thesis projects in this domain. Furthermore, the courses in the specialization on software analysis cover several verification techniques in depth. These courses include the application of verification to software projects and its theoretical foundations.

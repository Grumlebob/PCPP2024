package exercises10;

import benchmarking.Benchmark;
import java.util.stream.Stream;

public class TestIMDBStream {
    public static void main(String[] args) {
        //1. What is a stream?
        //Source, intermediate, sink
        //If parallel: ForkJoinPool(maxCores)

        String filename = ExamUtility.getResourcePath("episodes.tsv");

        //2. NO WORK, aka. I/O BOUND.
        Benchmark.Mark7("Sequential Stream", i -> {
            Stream<String> sequentialStream = ExamUtility.getLinesAsStream(filename);
            return sequentialStream.count();
        });

        Benchmark.Mark7("Parallel Stream", i -> {
            Stream<String> parallelStream = ExamUtility.getLinesAsParallelStream(filename);
            return parallelStream.count();
        });

        //3. WITH WORK, aka CPU BOUND - Count occurrences of 'e'
        Benchmark.Mark7("Sequential Stream with work (Count '5')", i -> {
            var stream = ExamUtility.getLinesAsStream(filename);
            return countCharacterInStream(stream, '5');
        });

        Benchmark.Mark7("Parallel Stream with work (Count '5')", i -> {
            var parallelStream = ExamUtility.getLinesAsParallelStream(filename);
            return countCharacterInStream(parallelStream, '5');
        });


        //4. RxJava
        // Java Stream: Pull. Lazy, One and done.
        // RxJava: Push. Observable. Different observers.
    }

    public static int countCharacterInStream(Stream<String> stream, char character) {
        return stream
                .mapToInt(line -> (int) line.chars().filter(ch -> ch == character).count())
                .sum();
    }
}

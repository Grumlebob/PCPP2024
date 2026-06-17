//Exercise 10.?
//JSt vers Oct 23, 2023

package exercises10;

import benchmarking.Benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestWordStream {
    public static void main(String[] args) {
        String filename = "C:\\Programming\\PCPP\\PCPP2024\\week10\\code-exercises\\week10exercises\\app\\src\\main\\resources\\english-words.txt";
        //10.2.1. Word count
        //PrintWordCount(filename);
        //10.2.2. First hundred words
        //printFirstHundredWords(filename);
        //10.2.3 - 22 letters, all words
        //AllLongWords(filename);
        //10.2.4 - 22 letters, some word
        //SomeLongWord(filename);
        //10.2.5 - Sequential palindromes
        SequentialPalindromes(filename);
        //10.2.6 - Parallel palindromes
        ParallelPalindromes(filename);
        //10.2.7 - Word count from URL
        //WordCountFromUrl();
        //10.2.8 - Word length statistics
        //MinMaxAverageStats(filename);
    }

    private static void PrintWordCount(String filename) {
        System.out.println("word count: " + readWords(filename).count());
    }

    private static void MinMaxAverageStats(String filename) {
        IntSummaryStatistics stats = readWords(filename)
                .mapToInt(String::length)
                .summaryStatistics();
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
        System.out.println("Average: " + stats.getAverage());
    }

    private static void WordCountFromUrl() {
        String url = "https://staunstrups.dk/jst/english-words.txt";
        long count = readWordStream(url).count();
        System.out.println("Number of words from URL: " + count);
    }

    private static void ParallelPalindromes(String filename) {
        Benchmark.Mark7("Parallel Palindromes", i -> findPalindromesParallel(filename));
    }

    private static void SequentialPalindromes(String filename) {
        Benchmark.Mark7("Sequential Palindromes", i -> findPalindromesSequential(filename));
    }

    private static void SomeLongWord(String filename) {
        readWords(filename).filter(word -> word.length() >= 22).findFirst().ifPresent(System.out::println);
    }

    private static void AllLongWords(String filename) {
        readWords(filename).filter(word -> word.length() >= 22).forEach(System.out::println);
    }


    public static Stream<String> readWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines();
        } catch (IOException exn) {
            exn.printStackTrace();
            return Stream.empty();
        }
    }

    public static Stream<String> readWordStream(String url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            return reader.lines();
        } catch (IOException exn) {
            exn.printStackTrace();
            return Stream.empty();
        }
    }


    public static void printFirstHundredWords(String filename) {
        readWords(filename).limit(100).forEach(System.out::println);
    }


    public static boolean isPalindrome(String s) {
        return s.equals(new StringBuilder(s).reverse().toString());
    }

    // Sequential version of the palindrome-printing stream pipeline
    public static long findPalindromesSequential(String filename) {
        return readWords(filename)
                .filter(TestWordStream::isPalindrome)
                .peek(System.out::println)
                .count();
    }

    // Parallel version of the palindrome-printing stream pipeline
    public static long findPalindromesParallel(String filename) {
        return readWords(filename)
                .parallel()
                .filter(TestWordStream::isPalindrome)
                .peek(System.out::println)
                .count();
    }

    //Bonus assignment
    public static Map<Character, Integer> letters(String s) {
        return s.chars()
                .filter(Character::isLetter) // Ensure only letters are considered
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        Character::toLowerCase, // Group by lowercase version of the character
                        TreeMap::new, // Use TreeMap to maintain sorted order
                        Collectors.summingInt(e -> 1) // Count occurrences
                ));
    }
}

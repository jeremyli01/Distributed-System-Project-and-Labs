package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.Scanner;

/**
 * This class implements a simple Spark application to analyze a text file, which reads in the file from Shakespeare's
 * All's Well That Ends Well and computes the following statistics:
 * number of lines, words, distinct words, symbols, distinct symbols, and distinct letters.
 *
 * In the end, the program also asks the user to enter a word and then prints out all the lines that contain the word.
 *
 * @version 1.0.0
 * @author Jeremy Li
 * @AndrewID zihanli2
 * */
public class ShakespeareAnalytics {

    public static void main(String[] args) {

        // Initialize Spark configuration and context
        SparkConf conf = new SparkConf().setAppName("ShortTextFileAnalyzer").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the input file as an RDD of lines
        JavaRDD<String> lines = sc.textFile(args[0]);

        // Task 0: count the number of lines
        long numLines = lines.count();
        // split the string using regex and then filter the empty strings
        JavaRDD<String> wordsFromFile = lines.flatMap(line -> Arrays.asList(line.split("[^a-zA-Z]+"))).filter(k -> (!k.isEmpty()));
        // Task 1: count the number of words
        long numWords = wordsFromFile.count();
        // Task 2: count the number of distinct words
        long numDistinctWords = wordsFromFile.distinct().count();
        JavaRDD<String> symbolFromFile = lines.flatMap(line -> Arrays.asList(line.split("")));
        // Task 3: count the number of symbols
        long numSymbols = symbolFromFile.count();
        // Task 4: count the number of distinct symbols
        long numDistinctSymbols = symbolFromFile.distinct().count();
        JavaRDD<String> letterFromFile = lines.flatMap(line -> Arrays.asList(line.replaceAll("[^a-zA-Z]", "").split(""))).filter(k -> (!k.isEmpty()));
        // Task 5: count the number of distinct letters
        long numDistinctLetters = letterFromFile.distinct().count();

        // Print the results
        System.out.println("Number of lines: " + numLines);
        System.out.println("Number of words: " + numWords);
        System.out.println("Number of distinct words: " + numDistinctWords);
        System.out.println("Number of symbols: " + numSymbols);
        System.out.println("Number of distinct symbols: " + numDistinctSymbols);
        System.out.println("Number of distinct letters: " + numDistinctLetters);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a word to search in All's Well That Ends Well: ");
        String word = scanner.nextLine();
        JavaRDD<String> lineWithTargetFromFile = lines.flatMap(content -> Arrays.asList(content.split("\n"))).filter(k -> k.contains(word));
        // print each line
        lineWithTargetFromFile.foreach(line -> System.out.println(line));
        // Stop the Spark context
        sc.stop();
    }
}

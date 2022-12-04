/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package aoc2022;

import java.util.Map;

import aoc2022.days.*;
import aoc2022.helpers.Day;
import aoc2022.helpers.Pair;

public class App {

    public static final String MISSING_ARGUMENT = "Missing argument (day/subday)";
    public static final String NO_FILE = "Could not load file";

    private static final Map<String, Pair<String, Day>> days = Map.of(
            "1a", new Pair<>("/input1.txt", Day1.day1a),
            "1b", new Pair<>("/input1.txt", Day1.day1b),
            "2a", new Pair<>("/input2.txt", Day2.day2a),
            "2b", new Pair<>("/input2.txt", Day2.day2b),
            "3a", new Pair<>("/input3.txt", Day3.day3a),
            "3b", new Pair<>("/input3.txt", Day3.day3b)
            );

    /**
     * Run a day quiz. Run with `./gradlew run --args="<day>"`
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            String day = args[0];
            System.out.println("Running AoC 2022 for " + day);
            Pair<String, Day> dayValues = days.get(day);
            System.out.println(dayValues.snd().apply(dayValues.fst()).orElse(NO_FILE));
        } else {
            System.out.println(MISSING_ARGUMENT);
        }
    }
}

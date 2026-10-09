/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 *
 * Modified for SE-314 Lab 05: the original version downloaded tweets from an
 * MIT server that is no longer running, so this version reads a small sample
 * of tweets from the file tweets.txt in the project folder instead.
 *
 * Main.java is not used in grading, so you are free to edit it as you wish.
 */
package twitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * This is the main program. It reads the sample tweets from tweets.txt and
 * prints some facts about them using Extract and Filter.
 */
public class Main {

    /** File of sample tweets, one tweet per line: id|author|timestamp|text */
    public static final String SAMPLE_FILE = "tweets.txt";

    /**
     * Main method of the program. Reads the sample tweets and prints some
     * facts about them.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            assert false;
            throw new Error("Always run main and tests with assertions enabled");
        } catch (AssertionError ae) { }

        final List<Tweet> tweets;
        try {
            tweets = readTweetsFromFile(SAMPLE_FILE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        System.out.println("read " + tweets.size() + " tweets from " + SAMPLE_FILE);
        for (Tweet tweet : tweets) {
            System.out.println("    " + tweet);
        }

        // Task 1: Extract
        final Timespan span = Extract.getTimespan(tweets);
        System.out.println();
        System.out.println("timespan: " + span.getStart() + " ... " + span.getEnd());

        final Set<String> mentionedUsers = Extract.getMentionedUsers(tweets);
        System.out.println("mentioned users: " + mentionedUsers);

        // Task 2: Filter
        System.out.println();
        System.out.println("tweets written by alyssa:");
        for (Tweet tweet : Filter.writtenBy(tweets, "alyssa")) {
            System.out.println("    " + tweet);
        }

        System.out.println();
        System.out.println("tweets containing \"rivest\" or \"pset\":");
        for (Tweet tweet : Filter.containing(tweets, Arrays.asList("rivest", "pset"))) {
            System.out.println("    " + tweet);
        }
    }

    /**
     * Read tweets from a file.
     *
     * @param filename name of a file with one tweet per line, in the format
     *                 id|author|timestamp|text
     * @return the list of tweets in the file, in the order they appear.
     * @throws IOException if the file cannot be read
     */
    private static List<Tweet> readTweetsFromFile(String filename) throws IOException {
        List<Tweet> tweets = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("[|]", 4);
            tweets.add(new Tweet(Long.parseLong(parts[0].trim()),
                                 parts[1].trim(),
                                 parts[3],
                                 Instant.parse(parts[2].trim())));
        }
        return tweets;
    }

}

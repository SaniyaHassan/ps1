/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy for guessFollowsGraph(tweets):
     *   number of tweets:                      0, 1, > 1
     *   mentions in a tweet:                   0, 1, > 1
     *   tweets by the same author:             1, > 1
     *   case of author and mentioned username: same, different
     *   author mentions themselves:            yes, no
     *
     * The postcondition is underdetermined. Usernames may be returned
     * in any case, an author with no follows may be absent from the graph
     * or present with an empty set, and an implementation may use evidence
     * beyond @-mentions. The tests therefore check only the follows that
     * every correct implementation MUST report, compare usernames without
     * regard to case, and never compare the whole graph with assertEquals.
     *
     * Testing strategy for influencers(followsGraph):
     *   number of users in the graph: 0, 1, > 1
     *   followers of the top user:    0, 1, > 1
     *   follower counts:              all different, some tied
     */

    private static final Instant D1 =
            Instant.parse("2016-02-17T10:00:00Z");

    private static final Instant D2 =
            Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet noMention = new Tweet(
            1, "alyssa",
            "is it reasonable to talk about rivest so much?",
            D1);

    private static final Tweet oneMention = new Tweet(
            2, "bbitdiddle",
            "saving a seat for @Alyssa",
            D1);

    private static final Tweet twoMentions = new Tweet(
            3, "evaluator",
            "thanks @bbitdiddle and @alyssa",
            D2);

    private static final Tweet bbitAgain = new Tweet(
            4, "BBitDiddle",
            "@evaluator see you at lecture",
            D2);

    private static final Tweet selfMention = new Tweet(
            5, "louis_r",
            "note to self @louis_r finish the lab report",
            D2);

    /*
     * Return true if follower follows followee in the graph, ignoring case.
     * This helper lives in the test class, not in SocialNetwork, so that the
     * tests stay legal clients of the published spec.
     */
    private static boolean follows(
            Map<String, Set<String>> graph,
            String follower,
            String followee) {

        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(follower)) {
                for (String name : entry.getValue()) {
                    if (name.equalsIgnoreCase(followee)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // 1. covers: 0 tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // 2. covers: 1 tweet, 0 mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(noMention));

        // alyssa may appear as a key with an empty set, so the graph is not
        // required to be empty; what matters is that nobody follows anybody.
        for (Set<String> followed : followsGraph.values()) {
            assertTrue("expected no follows", followed.isEmpty());
        }
    }

    // 3. covers: 1 tweet, 1 mention, different case
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(oneMention));

        assertTrue(
                "expected bbitdiddle follows alyssa",
                follows(followsGraph, "bbitdiddle", "alyssa"));
    }

    // 4. covers: 1 tweet, > 1 mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(twoMentions));

        assertTrue(
                "expected evaluator follows bbitdiddle",
                follows(followsGraph, "evaluator", "bbitdiddle"));

        assertTrue(
                "expected evaluator follows alyssa",
                follows(followsGraph, "evaluator", "alyssa"));
    }

    // 5. covers: > 1 tweets, same author in different case, different mentions
    @Test
    public void testGuessFollowsGraphMultipleTweetsOneUser() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(
                        Arrays.asList(oneMention, bbitAgain));

        assertTrue(
                "expected bbitdiddle follows alyssa",
                follows(followsGraph, "bbitdiddle", "alyssa"));

        assertTrue(
                "expected bbitdiddle follows evaluator",
                follows(followsGraph, "bbitdiddle", "evaluator"));
    }

    // 6. covers: author mentions themselves
    @Test
    public void testGuessFollowsGraphSelfMention() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(Arrays.asList(selfMention));

        // Users can't follow themselves, and this is the only mention in the
        // tweet, so no correct implementation may report a follow here.
        assertFalse(
                "expected louis_r not to follow himself",
                follows(followsGraph, "louis_r", "louis_r"));

        for (Set<String> followed : followsGraph.values()) {
            assertTrue("expected no follows", followed.isEmpty());
        }
    }

    // 7. covers: 0 users
    @Test
    public void testInfluencersEmpty() {
        List<String> influencers =
                SocialNetwork.influencers(new HashMap<>());

        assertTrue("expected empty list", influencers.isEmpty());
    }

    // 8. covers: 1 user, 0 followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<>());

        List<String> influencers =
                SocialNetwork.influencers(followsGraph);

        // The spec says the list holds ALL distinct usernames in the graph,
        // so alyssa is still listed even though she has no followers.
        assertEquals(1, influencers.size());
        assertEquals("alyssa", influencers.get(0).toLowerCase());
    }

    // 9. covers: > 1 users, top user has 1 follower
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put(
                "bbitdiddle",
                new HashSet<>(Arrays.asList("alyssa")));

        List<String> influencers =
                SocialNetwork.influencers(followsGraph);

        assertEquals(2, influencers.size());
        assertEquals("alyssa", influencers.get(0).toLowerCase());
    }

    // 10. covers: > 1 users, top user has > 1 followers, counts all different
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put(
                "bbitdiddle",
                new HashSet<>(Arrays.asList("alyssa")));

        followsGraph.put(
                "evaluator",
                new HashSet<>(Arrays.asList("alyssa", "bbitdiddle")));

        List<String> influencers =
                SocialNetwork.influencers(followsGraph);

        assertEquals(3, influencers.size());

        assertEquals("alyssa", influencers.get(0).toLowerCase());     // 2 followers
        assertEquals("bbitdiddle", influencers.get(1).toLowerCase()); // 1 follower
        assertEquals("evaluator", influencers.get(2).toLowerCase());  // 0 followers
    }

    // 11. covers: some follower counts tied
    @Test
    public void testInfluencersTied() {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        followsGraph.put(
                "evaluator",
                new HashSet<>(Arrays.asList("alyssa", "bbitdiddle")));

        List<String> influencers =
                SocialNetwork.influencers(followsGraph);

        assertEquals(3, influencers.size());

        // alyssa and bbitdiddle each have one follower. The spec does not say
        // which of them comes first, so either order is accepted here.
        Set<String> topTwo = new HashSet<>();
        topTwo.add(influencers.get(0).toLowerCase());
        topTwo.add(influencers.get(1).toLowerCase());

        assertEquals(
                new HashSet<>(Arrays.asList("alyssa", "bbitdiddle")),
                topTwo);

        assertEquals("evaluator", influencers.get(2).toLowerCase());
    }

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /*
     * Warning: all the tests written here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against
     * several staff implementations of SocialNetwork, which will be done by
     * overwriting (temporarily) this version of SocialNetwork with the
     * staff's version. DO NOT strengthen the spec of SocialNetwork or its
     * methods.
     *
     * In particular, these test cases must not call helper methods of my own
     * that I have put in SocialNetwork, because that means testing a stronger
     * spec than SocialNetwork says. Such helper methods are defined in this
     * test class instead.
     */

}

# SE-314 Software Construction - Lab 05: Test-First Programming (Tweet Tweet)

Problems 1 and 2 of [MIT 6.005 Problem Set 1](https://ocw.mit.edu/ans7870/6/6.005/s16/psets/ps1/),
done test-first: the JUnit tests were written and run (red bar) before any
method was implemented.

**Saniya Hassan - 518930 - BESE 15AB**

## Tasks

| Task | File | Methods |
| --- | --- | --- |
| Task 1 | `src/twitter/Extract.java` | `getTimespan`, `getMentionedUsers` |
| Task 2 | `src/twitter/Filter.java` | `writtenBy`, `inTimespan`, `containing` |

Tests are in `test/twitter/ExtractTest.java` (10 tests) and
`test/twitter/FilterTest.java` (13 tests). Each test has a comment naming the
partitions of the testing strategy that it covers.

## Test-first evidence

The commit history follows the order the lab requires:

1. `Add ps1 starter code from MIT 6.005 Problem Set 1` - methods still throw `not implemented`
2. `Read sample tweets from tweets.txt instead of the MIT server`
3. **`Add testing strategy and JUnit tests for Extract and Filter`** - at this commit
   22 of the 23 tests fail, which is the red bar
4. `Implement Extract.getTimespan and Extract.getMentionedUsers`
5. `Implement Filter.writtenBy, Filter.inTimespan and Filter.containing`

## Design decisions

- **Underdetermined specs.** `getMentionedUsers` may return a username in any
  case, so the implementation stores one lowercase copy and the tests compare
  through a `toLowerCase` helper kept inside the test class. `getTimespan` is
  not specified for an empty list, so that case is not tested.
- **Mentions.** The pattern `(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)` rejects an
  `@` that follows a username character, so `eval@mit.edu` is not a mention,
  and the greedy `+` makes sure no username character follows the match.
- **Timespan endpoints.** `inTimespan` uses `!isBefore(start) && !isAfter(end)`
  because a `Timespan` includes both of its endpoints.
- **Whole words.** `containing` splits the text on spaces and compares whole
  words with `equalsIgnoreCase`, so `"Talking"` does not match the word
  `"talk"`.

## Running it

Import into Eclipse as an existing project (JUnit 4 is on the build path).
Run `ExtractTest` and `FilterTest` as JUnit tests, or right-click the `test`
folder to run all 23 at once. `Main.java` reads the 8 sample tweets in
`tweets.txt` and prints the timespan, the mentioned users, the tweets by
`alyssa` and the tweets containing `rivest` or `pset`. Run it with the VM
argument `-ea` so that assertions are enabled.

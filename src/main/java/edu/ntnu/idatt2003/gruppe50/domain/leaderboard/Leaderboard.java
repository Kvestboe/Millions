package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores leaderboard entries and keeps the best scores for each difficulty.
 */
public class Leaderboard {

  private static final int MAX_PER_DIFFICULTY = 10;
  private final List<LeaderboardEntry> entries = new ArrayList<>();

  /**
   * Returns the best entries for the given difficulty.
   *
   * @param difficulty the difficulty to get entries for
   * @return the top leaderboard entries for the difficulty
   */
  public List<LeaderboardEntry> top(Difficulty difficulty) {
    return entries.stream()
        .filter(e -> e.difficulty() == difficulty)
        .sorted(Comparator.comparingDouble(LeaderboardEntry::score).reversed())
        .limit(MAX_PER_DIFFICULTY)
        .toList();
  }

  /**
   * Adds a new entry to the leaderboard.
   *
   * @param entry the entry to add
   * @return true if the entry is still on the leaderboard after trimming
   */
  public boolean add(LeaderboardEntry entry) {
    entries.add(entry);
    trim(entry.difficulty());
    return top(entry.difficulty()).contains(entry);
  }

  /**
   * Returns all leaderboard entries.
   *
   * @return all entries in the leaderboard
   */
  public List<LeaderboardEntry> all() {
    return List.copyOf(entries);
  }

  /**
   * Replaces all current entries with loaded entries.
   *
   * @param loaded the entries to use
   */
  public void replaceAll(List<LeaderboardEntry> loaded) {
    entries.clear();
    entries.addAll(loaded);
  }

  private void trim(Difficulty difficulty) {
    Set<LeaderboardEntry> keep = new HashSet<>(top(difficulty));
    entries.removeIf(e -> e.difficulty() == difficulty && !keep.contains(e));
  }
}
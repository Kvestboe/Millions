package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Leaderboard {

  private static final int MAX_PER_DIFFICULTY = 10;

  private final List<LeaderboardEntry> entries = new ArrayList<>();

  public List<LeaderboardEntry> top(Difficulty difficulty) {
    return entries.stream()
        .filter(e -> e.difficulty() == difficulty)
        .sorted(Comparator.comparingDouble(LeaderboardEntry::score).reversed())
        .limit(MAX_PER_DIFFICULTY)
        .toList();
  }

  public boolean add(LeaderboardEntry entry) {
    entries.add(entry);
    trim(entry.difficulty());
    return top(entry.difficulty()).contains(entry);
  }

  public List<LeaderboardEntry> all() {
    return List.copyOf(entries);
  }

  public void replaceAll(List<LeaderboardEntry> loaded) {
    entries.clear();
    entries.addAll(loaded);
  }

  private void trim(Difficulty difficulty) {
    Set<LeaderboardEntry> keep = new HashSet<>(top(difficulty));
    entries.removeIf(e -> e.difficulty() == difficulty && !keep.contains(e));
  }
}
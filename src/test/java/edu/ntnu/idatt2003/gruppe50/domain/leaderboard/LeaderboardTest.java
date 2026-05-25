package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LeaderboardTest {

  private Leaderboard leaderboard;

  @BeforeEach
  void setUp() {
    leaderboard = new Leaderboard();
  }

  private LeaderboardEntry entry(String name, double score, Difficulty difficulty) {
    return new LeaderboardEntry(name, score, 10, BigDecimal.valueOf(1_000_000),
        BigDecimal.valueOf(10_000), difficulty, LocalDate.now());
  }

  @Test
  void add_singleEntry_isInTop() {
    LeaderboardEntry e = entry("Alice", 500, Difficulty.EASY);
    assertTrue(leaderboard.add(e));
  }

  @Test
  void add_singleEntry_appearsInTop() {
    LeaderboardEntry e = entry("Alice", 500, Difficulty.EASY);
    leaderboard.add(e);
    assertTrue(leaderboard.top(Difficulty.EASY).contains(e));
  }

  @Test
  void top_sortedDescendingByScore() {
    LeaderboardEntry low = entry("Low", 100, Difficulty.EASY);
    LeaderboardEntry high = entry("High", 900, Difficulty.EASY);
    leaderboard.add(low);
    leaderboard.add(high);

    List<LeaderboardEntry> top = leaderboard.top(Difficulty.EASY);
    assertEquals(high, top.get(0));
    assertEquals(low, top.get(1));
  }

  @Test
  void add_eleventhEntry_lowestScoreIsDropped() {
    for (int i = 1; i <= 10; i++) {
      leaderboard.add(entry("Player" + i, i * 100.0, Difficulty.EASY));
    }

    LeaderboardEntry newcomer = entry("Newcomer", 550, Difficulty.EASY);
    boolean inTop = leaderboard.add(newcomer);

    assertTrue(inTop);
    assertEquals(10, leaderboard.top(Difficulty.EASY).size());
  }

  @Test
  void add_eleventhEntryLowestScore_isNotInTop() {
    for (int i = 1; i <= 10; i++) {
      leaderboard.add(entry("Player" + i, i * 100.0, Difficulty.EASY));
    }

    LeaderboardEntry weak = entry("Weak", 50, Difficulty.EASY);
    boolean inTop = leaderboard.add(weak);

    assertFalse(inTop);
    assertFalse(leaderboard.top(Difficulty.EASY).contains(weak));
  }

  @Test
  void top_differentDifficulties_doNotInterfere() {
    LeaderboardEntry easy = entry("Easy", 500, Difficulty.EASY);
    LeaderboardEntry hard = entry("Hard", 500, Difficulty.HARD);
    leaderboard.add(easy);
    leaderboard.add(hard);

    assertTrue(leaderboard.top(Difficulty.EASY).contains(easy));
    assertFalse(leaderboard.top(Difficulty.EASY).contains(hard));
    assertTrue(leaderboard.top(Difficulty.HARD).contains(hard));
    assertFalse(leaderboard.top(Difficulty.HARD).contains(easy));
  }

  @Test
  void top_emptyLeaderboard_returnsEmptyList() {
    assertTrue(leaderboard.top(Difficulty.MEDIUM).isEmpty());
  }

  @Test
  void replaceAll_replacesExistingEntries() {
    leaderboard.add(entry("Old", 999, Difficulty.EASY));

    LeaderboardEntry replacement = entry("New", 100, Difficulty.MEDIUM);
    leaderboard.replaceAll(List.of(replacement));

    assertTrue(leaderboard.top(Difficulty.EASY).isEmpty());
    assertTrue(leaderboard.top(Difficulty.MEDIUM).contains(replacement));
  }

  @Test
  void all_returnsAllEntries() {
    LeaderboardEntry e1 = entry("A", 100, Difficulty.EASY);
    LeaderboardEntry e2 = entry("B", 200, Difficulty.HARD);
    leaderboard.add(e1);
    leaderboard.add(e2);

    assertEquals(2, leaderboard.all().size());
  }

  @Nested
  class ScoreCalculationTests {

    @Test
    void calculateScore_zeroWeeks_returnsZero() {
      assertEquals(0.0, LeaderboardEntry.calculateScore(0, BigDecimal.valueOf(10_000)), 0.001);
    }

    @Test
    void calculateScore_negativeWeeks_returnsZero() {
      assertEquals(0.0, LeaderboardEntry.calculateScore(-1, BigDecimal.valueOf(10_000)), 0.001);
    }

    @Test
    void calculateScore_nullCapital_returnsZero() {
      assertEquals(0.0, LeaderboardEntry.calculateScore(10, null), 0.001);
    }

    @Test
    void calculateScore_zeroCapital_returnsZero() {
      assertEquals(0.0, LeaderboardEntry.calculateScore(10, BigDecimal.ZERO), 0.001);
    }

    @Test
    void calculateScore_lowerCapital_givesHigherScore() {
      double low = LeaderboardEntry.calculateScore(10, BigDecimal.valueOf(100_000));
      double high = LeaderboardEntry.calculateScore(10, BigDecimal.valueOf(500_000));

      assertTrue(low > high);
    }

    @Test
    void calculateScore_validInput_returnsPositiveScore() {
      assertTrue(LeaderboardEntry.calculateScore(10, BigDecimal.valueOf(10_000)) > 0);
    }

    @Test
    void calculateScore_fewerWeeks_givesHigherScore() {
      double fast = LeaderboardEntry.calculateScore(5, BigDecimal.valueOf(10_000));
      double slow = LeaderboardEntry.calculateScore(50, BigDecimal.valueOf(10_000));

      assertTrue(fast > slow);
    }
  }
}

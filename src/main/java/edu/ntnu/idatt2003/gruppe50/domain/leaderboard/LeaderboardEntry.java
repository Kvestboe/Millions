package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LeaderboardEntry(
    String playerName,
    double score,
    int weeksPlayed,
    BigDecimal finalNetWorth,
    BigDecimal startingCapital,
    Difficulty difficulty,
    LocalDate completedDate
) {
  public static double calculateScore(int weeks, BigDecimal startingCapital) {
    if (weeks <= 0 || startingCapital == null || startingCapital.signum() <= 0) {
      return 0.0;
    }
    // Score formula made with help from AI.
    double capitalFactor = Math.pow(1_000_000.0 / startingCapital.doubleValue(), 1.5);
    double weekBonus = 100.0 / Math.sqrt(weeks);
    return capitalFactor * weekBonus;
  }
}

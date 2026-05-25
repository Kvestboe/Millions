package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents one entry on the leaderboard.
 *
 * @param playerName the name of the player
 * @param score the score the player got
 * @param weeksPlayed the number of weeks played
 * @param finalNetWorth the player's final net worth
 * @param startingCapital the amount of money the player started with
 * @param difficulty the difficulty used in the game
 * @param completedDate the date the game was completed
 */
public record LeaderboardEntry(
    String playerName,
    double score,
    int weeksPlayed,
    BigDecimal finalNetWorth,
    BigDecimal startingCapital,
    Difficulty difficulty,
    LocalDate completedDate
) {
  /**
   * Calculates a leaderboard score.
   *
   * @param weeks the number of weeks played
   * @param startingCapital the amount of money the player started with
   * @return the calculated score, or 0 if the input is invalid
   */
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

package edu.ntnu.idatt2003.gruppe50.domain.leaderboard;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameResult;
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
   * Calculates a leaderboard score by delegating to {@link GameResult}.
   *
   * <p>Both code paths must produce identical scores; routing them through one
   * implementation keeps the formula in a single place.
   *
   * @param weeks the number of weeks played
   * @param startingCapital the amount of money the player started with
   * @return the calculated score, or 0 if the input is invalid
   */
  public static double calculateScore(int weeks, BigDecimal startingCapital) {
    return new GameResult(true, startingCapital, startingCapital, weeks, Difficulty.EASY)
        .calculateScore();
  }
}

package edu.ntnu.idatt2003.gruppe50.domain.game;

import java.math.BigDecimal;

/**
 * Represents the final result of a completed game.
 *
 * @param won whether the player won the game
 * @param finalNetWorth the player's final net worth
 * @param startingCapital the amount of money the player started with
 * @param weeksPlayed the number of weeks played
 * @param difficulty the difficulty used in the game
 */
public record GameResult(boolean won,
                         BigDecimal finalNetWorth,
                         BigDecimal startingCapital,
                         int weeksPlayed,
                         Difficulty difficulty) {

  /**
   * Calculates the player's score based on the result.
   *
   * @return the calculated score, or 0 if the game was lost
   */
  public long calculateScore() {
    if (!won || weeksPlayed <= 0 || startingCapital == null || startingCapital.signum() <= 0) {
      return 0;
    }
    // Score formula made with help from AI.
    double capitalFactor = Math.pow(1_000_000.0 / startingCapital.doubleValue(), 1.5);
    double weekBonus = 100.0 / Math.sqrt(weeksPlayed);
    return Math.round(capitalFactor * weekBonus);
  }
}

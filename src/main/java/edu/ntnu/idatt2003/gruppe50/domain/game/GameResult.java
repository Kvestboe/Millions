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
    if (!won) {
      return 0;
    }
    double weekFactor = Math.pow(weeksPlayed, 1.5);
    double capitalFactor = Math.sqrt(10_000.0 / startingCapital.doubleValue());
    return Math.round((1_000_000.0 / weekFactor) * capitalFactor);
  }
}

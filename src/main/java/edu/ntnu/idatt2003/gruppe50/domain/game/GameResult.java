package edu.ntnu.idatt2003.gruppe50.domain.game;

import java.math.BigDecimal;

/**
 * Represents the final result of a completed game.
 *
 * @param won             whether the player won the game
 * @param finalNetWorth   the player's final net worth
 * @param startingCapital the amount of money the player started with
 * @param weeksPlayed     the number of weeks played
 * @param difficulty      the difficulty used in the game
 */
public record GameResult(boolean won,
                         BigDecimal finalNetWorth,
                         BigDecimal startingCapital,
                         int weeksPlayed,
                         Difficulty difficulty) {

  private static final double WIN_TARGET = 1_000_000.0;
  private static final double CAPITAL_EXPONENT = 1.5;
  private static final double WEEK_BONUS_BASE = 100.0;

  /**
   * Calculates the player's score based on the result.
   *
   * <p>Scoring rewards finishing with less starting capital and in fewer weeks:
   * the capital factor scales as {@code (WIN_TARGET / startingCapital)^1.5},
   * and the week bonus shrinks as {@code 100 / sqrt(weeksPlayed)}.
   *
   * @return the calculated score, or 0 if the game was lost or input is invalid
   */
  public long calculateScore() {
    if (!won || weeksPlayed <= 0 || startingCapital == null || startingCapital.signum() <= 0) {
      return 0;
    }
    double capitalFactor = Math.pow(WIN_TARGET / startingCapital.doubleValue(), CAPITAL_EXPONENT);
    double weekBonus = WEEK_BONUS_BASE / Math.sqrt(weeksPlayed);
    return Math.round(capitalFactor * weekBonus);
  }
}

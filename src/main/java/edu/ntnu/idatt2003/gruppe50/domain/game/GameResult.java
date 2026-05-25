package edu.ntnu.idatt2003.gruppe50.domain.game;

import java.math.BigDecimal;

public record GameResult(boolean won,
                         BigDecimal finalNetWorth,
                         BigDecimal startingCapital,
                         int weeksPlayed,
                         Difficulty difficulty) {

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

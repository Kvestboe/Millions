package edu.ntnu.idatt2003.gruppe50.domain.game;

import java.math.BigDecimal;

public record GameResult(boolean won,
                         BigDecimal finalNetWorth,
                         BigDecimal startingCapital,
                         int weeksPlayed,
                         Difficulty difficulty) {

  public long calculateScore() {
    if (!won) return 0;
    double weekFactor = Math.pow(weeksPlayed, 1.5);
    double capitalFactor = Math.sqrt(10_000.0 / startingCapital.doubleValue());
    return Math.round((1_000_000.0 / weekFactor) * capitalFactor);
  }
}

package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import java.math.BigDecimal;

/**
 * Represents the player's status level.
 */
public enum Status {
  /**
   * The first status level.
   */
  NOVICE(0, BigDecimal.ZERO),

  /**
   * The second status level.
   */
  INVESTOR(10, new BigDecimal("0.20")),

  /**
   * The highest status level.
   */
  SPECULATOR(20, new BigDecimal("1.00"));

  private final int requiredWeeks;
  private final BigDecimal requiredGain;   // andel: 0.20 = +20 %

  Status(int requiredWeeks, BigDecimal requiredGain) {
    this.requiredWeeks = requiredWeeks;
    this.requiredGain = requiredGain;
  }

  /**
   * Returns the number of trading weeks required for this status.
   *
   * @return the required number of weeks
   */
  public int getRequiredWeeks() {
    return requiredWeeks;
  }

  /**
   * Returns the required gain for this status.
   *
   * @return the required gain as a decimal value
   */
  public BigDecimal getRequiredGain() {
    return requiredGain;
  }

  /**
   * Returns the next status level.
   *
   * @return the next status, or null if this is the highest status
   */
  public Status next() {
    return switch (this) {
      case NOVICE -> INVESTOR;
      case INVESTOR -> SPECULATOR;
      case SPECULATOR -> null;
    };
  }

  /**
   * Returns a readable name for the status.
   *
   * @return the display name
   */
  public String displayName() {
    return name().charAt(0) + name().substring(1).toLowerCase();
  }
}
package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import java.math.BigDecimal;

public enum Status {
  NOVICE(0, BigDecimal.ZERO),
  INVESTOR(10, new BigDecimal("0.20")),
  SPECULATOR(20, new BigDecimal("1.00"));

  private final int requiredWeeks;
  private final BigDecimal requiredGain;   // andel: 0.20 = +20 %

  Status(int requiredWeeks, BigDecimal requiredGain) {
    this.requiredWeeks = requiredWeeks;
    this.requiredGain = requiredGain;
  }

  public int getRequiredWeeks() {
    return requiredWeeks;
  }

  public BigDecimal getRequiredGain() {
    return requiredGain;
  }

  public Status next() {
    return switch (this) {
      case NOVICE -> INVESTOR;
      case INVESTOR -> SPECULATOR;
      case SPECULATOR -> null;
    };
  }

  public String displayName() {
    return name().charAt(0) + name().substring(1).toLowerCase();
  }
}
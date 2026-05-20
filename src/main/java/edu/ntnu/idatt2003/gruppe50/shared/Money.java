package edu.ntnu.idatt2003.gruppe50.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Centralised scale management for monetary BigDecimal values.
 *
 * <p>BigDecimal arithmetic preserves and combines scales — repeated multiplications
 * can produce values with hundreds of decimal places. Routing values through
 * {@link #round(BigDecimal)} caps the scale at a sensible precision.
 */
public final class Money {

  public static final int SCALE = 4;

  private Money() {}

  public static BigDecimal round(BigDecimal value) {
    return value.setScale(SCALE, RoundingMode.HALF_UP);
  }
}

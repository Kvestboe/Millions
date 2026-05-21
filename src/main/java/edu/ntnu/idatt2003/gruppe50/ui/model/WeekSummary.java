package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.List;

public record WeekSummary(
    int previousWeek,
    int newWeek,
    BigDecimal netWorthBefore,
    BigDecimal netWorthAfter,
    BigDecimal cash,
    BigDecimal hangarCost,
    List<WeekHolding> holdings,
    List<String> news,
    List<String> notifications
) {
  public BigDecimal weeklyDelta() {
    return netWorthAfter.subtract(netWorthBefore);
  }

  /**
   * Returns the portfolio movement before weekly hangar cost is deducted.
   *
   * @return weekly net worth delta plus the hangar cost
   */
  public BigDecimal portfolioMovement() {
    return weeklyDelta().add(hangarCost);
  }

  /**
   * Returns the gross movement of all holdings before fees, tax and hangar cost.
   *
   * @return the sum of all holding deltas for the week
   */
  public BigDecimal holdingsMovement() {
    return holdings.stream()
        .map(WeekHolding::weeklyDelta)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Returns the impact from fees and tax on the weekly portfolio movement.
   *
   * <p>This is the difference between the net portfolio movement and the gross
   * holdings movement. It is usually negative when profitable holdings are taxed
   * or affected by sales commission.
   *
   * @return fees and tax impact for the week
   */
  public BigDecimal feesAndTaxImpact() {
    return portfolioMovement().subtract(holdingsMovement());
  }
}
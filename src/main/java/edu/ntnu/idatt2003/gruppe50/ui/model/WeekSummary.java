package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * UI model for the summary shown after advancing a week.
 *
 * @param previousWeek   week before advancing
 * @param newWeek        week after advancing
 * @param netWorthBefore net worth before the week advanced
 * @param netWorthAfter  net worth after the week advanced
 * @param cash           player cash after the week advanced
 * @param hangarCost     weekly hangar cost
 * @param holdings       per-holding weekly movement
 * @param news           market news messages
 * @param notifications  gameplay notifications
 */
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

  /**
   * Returns the net worth change for the week.
   *
   * @return net worth after minus net worth before
   */
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
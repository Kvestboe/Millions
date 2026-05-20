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
}
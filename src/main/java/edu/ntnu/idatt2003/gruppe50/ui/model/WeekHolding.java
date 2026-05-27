package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;

/**
 * UI model for one holding's weekly movement.
 *
 * @param symbol        stock symbol
 * @param quantity      owned quantity
 * @param weeklyDelta   value change during the week
 * @param weeklyPercent percentage change during the week
 */
public record WeekHolding(
    String symbol,
    BigDecimal quantity,
    BigDecimal weeklyDelta,
    BigDecimal weeklyPercent
) {
}

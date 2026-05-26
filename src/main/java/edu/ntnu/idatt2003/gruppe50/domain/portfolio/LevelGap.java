package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import java.math.BigDecimal;

/**
 * Describes what the player needs to reach the next status level.
 *
 * @param weeksRemaining the number of weeks still needed
 * @param moneyRemaining the amount of money still needed
 */
public record LevelGap(int weeksRemaining, BigDecimal moneyRemaining) {}

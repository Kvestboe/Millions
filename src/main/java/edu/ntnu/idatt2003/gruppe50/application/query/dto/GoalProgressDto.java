package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;

/**
 * DTO representing the player's progress towards the win threshold.
 *
 * @param currentNetWorth the player's current net worth
 * @param goal the net worth required to win the game
 * @param progress the fraction of the goal reached, clamped to [0, 1]
 */
public record GoalProgressDto(
    BigDecimal currentNetWorth,
    BigDecimal goal,
    double progress
) {}
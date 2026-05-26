package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;

/**
 * DTO representing the player's progress towards the next status level.
 *
 * @param currentStatus the player's current status display name (e.g. "Novice")
 * @param nextStatus the next status display name, or {@code null} if at max level
 * @param progress the fraction of progress towards the next status, in [0, 1]
 * @param weeksRemaining number of weeks needed to reach the next status
 * @param moneyRemaining net worth required to reach the next status
 * @param atMaxLevel true if the player has reached the highest status
 */
public record StatusProgressDto(
    String currentStatus,
    String nextStatus,
    double progress,
    int weeksRemaining,
    BigDecimal moneyRemaining,
    boolean atMaxLevel
) {}
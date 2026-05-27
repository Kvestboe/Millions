package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Summary data for a saved game session.
 *
 * @param gameId     id of the saved game session
 * @param playerName name of the player in the save
 * @param week       current week in the saved session
 * @param status     display status for the player
 * @param netWorth   player's net worth in the save
 * @param lastPlayed timestamp for when the save was last opened or updated
 * @param isFinished whether the saved session is finished
 */
public record SaveSummaryDto(
    UUID gameId,
    String playerName,
    int week,
    String status,
    BigDecimal netWorth,
    LocalDateTime lastPlayed,
    boolean isFinished
) {
}

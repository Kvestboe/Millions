package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serializable root object for a saved game session.
 *
 * @param gameId saved game-session id
 * @param state saved session state
 * @param difficulty saved difficulty level
 * @param runStartedAt timestamp for when the run started
 * @param lastPlayed timestamp for when the session was last played
 * @param netWorthHistory saved net-worth history
 * @param player saved player state
 * @param exchange saved exchange state
 * @param coinExchange saved coin-exchange state (may be {@code null} for legacy
 *     save files that pre-date persistent coin pricing)
 */
public record GameSaveDto(
    String gameId,
    String state,
    String difficulty,
    String runStartedAt,
    String lastPlayed,
    List<BigDecimal> netWorthHistory,
    PlayerDto player,
    ExchangeDto exchange,
    CoinExchangeDto coinExchange
) {

}

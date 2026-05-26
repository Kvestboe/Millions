package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

/**
 * Serializable transaction state for a saved game.
 *
 * @param type transaction type
 * @param stockSymbol stock symbol involved in the transaction
 * @param quantity transaction quantity
 * @param purchasePrice purchase price used by the transaction
 * @param week week when the transaction occurred
 * @param purchaseWeek original purchase week for the share lot
 * @param batchId id grouping related transactions
 */
public record TransactionDto(
    String type,
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    int week,
    int purchaseWeek,
    String batchId
) {
}

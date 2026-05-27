package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

/**
 * Serializable share state for a saved game.
 *
 * @param stockSymbol   symbol of the owned stock
 * @param quantity      owned quantity
 * @param purchasePrice purchase price per share
 * @param purchaseWeek  week when the share was purchased
 */
public record ShareDto(
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    int purchaseWeek
) {

}

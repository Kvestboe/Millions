package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

/**
 * Serializable pending order state for a saved game.
 *
 * @param type order type label
 * @param stockSymbol stock symbol for the order
 * @param targetPrice target or trigger price
 * @param quantity order quantity
 * @param createdWeek week when the order was created
 * @param expiryWeek week when the order expires
 */
public record LimitOrderDto(
    String type,
    String stockSymbol,
    BigDecimal targetPrice,
    BigDecimal quantity,
    int createdWeek,
    int expiryWeek
) {

}

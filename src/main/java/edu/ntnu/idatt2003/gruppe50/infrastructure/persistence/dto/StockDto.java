package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serializable stock state for a saved game.
 *
 * @param symbol stock symbol
 * @param company company name
 * @param prices historical prices
 */
public record StockDto(
    String symbol,
    String company,
    List<BigDecimal> prices
) {

}

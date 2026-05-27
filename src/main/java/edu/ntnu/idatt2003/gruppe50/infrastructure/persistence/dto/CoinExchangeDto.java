package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serializable coin exchange state for a saved game.
 *
 * <p>The minimum and maximum prices are not persisted because they are derived
 * from the player's starting capital, which is already stored on
 * {@link PlayerDto}. Rehydration recomputes them from the starting capital and
 * restores the {@code priceHistory} verbatim.
 *
 * @param priceHistory historical coin prices in chronological order
 */
public record CoinExchangeDto(
    List<BigDecimal> priceHistory
) {

}

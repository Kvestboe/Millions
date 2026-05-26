package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.util.List;

/**
 * Serializable exchange state for a saved game.
 *
 * @param name exchange name
 * @param week current exchange week
 * @param stocks saved stocks
 * @param pendingOrders saved pending orders
 */
public record ExchangeDto(
    String name,
    int week,
    List<StockDto> stocks,
    List<LimitOrderDto> pendingOrders
) {

}

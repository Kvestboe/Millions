package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;

/**
 * Pending order data exposed to the UI layer.
 *
 * @param type display label for the order type
 * @param symbol stock symbol for the order
 * @param company company name for the stock
 * @param quantity order quantity
 * @param targetPrice target or trigger price for the order
 * @param createdWeek week when the order was created
 * @param expiryWeek week when the order expires
 */
public record PendingOrderDto(
    String type,
    String symbol,
    String company,
    BigDecimal quantity,
    BigDecimal targetPrice,
    int createdWeek,
    int expiryWeek
) {}

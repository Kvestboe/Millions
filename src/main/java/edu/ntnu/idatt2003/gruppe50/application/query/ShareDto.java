package edu.ntnu.idatt2003.gruppe50.application.query;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Share row DTO for portfolio responses.
 *
 * @param shareId unique share id
 * @param symbol stock symbol
 * @param stock company name
 * @param quantity owned quantity
 * @param purchasePrice original unit purchase price
 * @param currentPrice current unit market price
 * @param currentShareValue current gross value of this share position
 */
public record ShareDto(
    UUID shareId,
    String symbol,
    String stock,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    BigDecimal currentPrice,
    BigDecimal currentShareValue
) {}

package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * UI model for a share holding.
 *
 * @param shareId unique share id
 * @param symbol stock symbol
 * @param stock company name
 * @param quantity owned quantity
 * @param purchasePrice original or weighted average purchase price
 * @param currentPrice current market price
 * @param currentShareValue current gross value of the holding
 * @param gain unrealized gain or loss
 * @param percentageGain unrealized percentage gain or loss
 */
public record ShareData(
    UUID shareId,
    String symbol,
    String stock,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    BigDecimal currentPrice,
    BigDecimal currentShareValue,
    BigDecimal gain,
    BigDecimal percentageGain
) {}

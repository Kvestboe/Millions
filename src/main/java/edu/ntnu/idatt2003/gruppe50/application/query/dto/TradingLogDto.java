package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Trading log summary exposed to the UI layer.
 *
 * @param totalTrades    total number of transactions
 * @param purchases      number of purchase transactions
 * @param sales          number of sale transactions
 * @param totalFees      total commission fees paid
 * @param totalTaxes     total taxes paid
 * @param realizedPnL    realized profit or loss
 * @param recentActivity most recent transactions
 */
public record TradingLogDto(
    int totalTrades,
    long purchases,
    long sales,
    BigDecimal totalFees,
    BigDecimal totalTaxes,
    BigDecimal realizedPnL,
    List<TransactionDto> recentActivity
) {
}

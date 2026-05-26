package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * UI model for trading log summary data.
 *
 * @param totalTrades total number of trades
 * @param purchases number of purchase trades
 * @param sales number of sale trades
 * @param totalFees total commission fees
 * @param totalTaxes total tax amount
 * @param realizedPnL realized profit or loss
 * @param recentActivity recent transactions
 */
public record TradingLogData(
    int totalTrades,
    long purchases,
    long sales,
    BigDecimal totalFees,
    BigDecimal totalTaxes,
    BigDecimal realizedPnL,
    List<TransactionData> recentActivity
) {}

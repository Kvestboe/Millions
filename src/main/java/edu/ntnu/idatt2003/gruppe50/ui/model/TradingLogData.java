package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.List;

public record TradingLogData(
    int totalTrades,
    long purchases,
    long sales,
    BigDecimal totalFees,
    BigDecimal totalTaxes,
    BigDecimal realizedPnL,
    List<TransactionData> recentActivity
) {}

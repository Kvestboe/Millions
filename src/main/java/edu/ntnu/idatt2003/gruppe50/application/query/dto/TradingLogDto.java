package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.util.List;

public record TradingLogDto(
    int totalTrades,
    long purchases,
    long sales,
    BigDecimal totalFees,
    BigDecimal totalTaxes,
    BigDecimal realizedPnL,
    List<TransactionDto> recentActivity
) {}

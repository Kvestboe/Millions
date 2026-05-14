package edu.ntnu.idatt2003.gruppe50.application.query;

import java.math.BigDecimal;

public record PendingOrderDto (
    String type,
    String symbol,
    String company,
    BigDecimal quantity,
    BigDecimal targetPrice,
    int createdWeek,
    int expiryWeek
) {}

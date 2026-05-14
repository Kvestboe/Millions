package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;

public record OrderData(
    String type,
    String symbol,
    String company,
    BigDecimal quantity,
    BigDecimal targetPrice,
    int createdWeek,
    int expiryWeek
) {
}

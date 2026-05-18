package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

public record LimitOrderDto(
    String type,
    String stockSymbol,
    BigDecimal targetPrice,
    BigDecimal quantity,
    int createdWeek,
    int expiryWeek
) {

}

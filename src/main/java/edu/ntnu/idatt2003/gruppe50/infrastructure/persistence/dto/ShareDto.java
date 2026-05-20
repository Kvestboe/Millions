package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

public record ShareDto(
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    int purchaseWeek
) {

}

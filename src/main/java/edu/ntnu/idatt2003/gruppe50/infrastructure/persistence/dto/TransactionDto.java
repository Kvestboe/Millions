package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;

public record TransactionDto(
    String type,
    String stockSymbol,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    int week,
    int purchaseWeek,
    String batchId
) {

}

package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;

public record TransactionDto(
    ShareDto share,
    int week,
    TransactionType type,
    boolean committed,
    BigDecimal taxFee,
    BigDecimal commissionFee,
    BigDecimal total
){}

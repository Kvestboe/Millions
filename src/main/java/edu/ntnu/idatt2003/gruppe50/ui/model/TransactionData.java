package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.TransactionType;
import java.math.BigDecimal;

public record TransactionData(
    ShareData share,
    int week,
    TransactionType type,
    boolean committed,
    BigDecimal taxFee,
    BigDecimal commissionFee,
    BigDecimal total
) {}

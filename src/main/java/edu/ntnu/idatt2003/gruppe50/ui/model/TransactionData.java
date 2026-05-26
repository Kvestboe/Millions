package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionType;
import java.math.BigDecimal;

/**
 * UI model for a transaction row.
 *
 * @param share share involved in the transaction
 * @param week week when the transaction occurred
 * @param type transaction type
 * @param committed whether the transaction has been committed
 * @param taxFee tax amount
 * @param commissionFee commission amount
 * @param total total transaction amount
 */
public record TransactionData(
    ShareData share,
    int week,
    TransactionType type,
    boolean committed,
    BigDecimal taxFee,
    BigDecimal commissionFee,
    BigDecimal total
) {}

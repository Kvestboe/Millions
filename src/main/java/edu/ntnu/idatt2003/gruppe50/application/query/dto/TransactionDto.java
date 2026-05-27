package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;

/**
 * Transaction data exposed to the UI layer.
 *
 * @param share         share involved in the transaction
 * @param week          week when the transaction occurred
 * @param type          transaction type
 * @param committed     whether the transaction has been committed to the portfolio
 * @param taxFee        tax amount for the transaction
 * @param commissionFee commission amount for the transaction
 * @param total         total transaction amount after fees and taxes
 */
public record TransactionDto(
    ShareDto share,
    int week,
    TransactionType type,
    boolean committed,
    BigDecimal taxFee,
    BigDecimal commissionFee,
    BigDecimal total
) {
}

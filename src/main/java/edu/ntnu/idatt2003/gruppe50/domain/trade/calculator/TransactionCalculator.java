package edu.ntnu.idatt2003.gruppe50.domain.trade.calculator;

import java.math.BigDecimal;

/**
 * Represents a calculator which does the key calculations when
 * doing transactions like purchasing and selling shares.
 */
public interface TransactionCalculator {

  /**
   * Calculates the gross amount of the transaction before deductions such as commission or tax.
   *
   * @return the gross transaction amount as a {@link BigDecimal}
   */
  BigDecimal calculateGross();

  /**
   * Calculates the commission fee for the transaction.
   *
   * @return the commission fee as a {@link BigDecimal}
   */
  BigDecimal calculateCommission();

  /**
   * Calculates the tax applied to the transaction.
   *
   * @return the tax amount as a {@link BigDecimal}
   */
  BigDecimal calculateTax();

  /**
   * Calculates the total amount of the transaction
   * after applying relevant fees defined in the model.
   *
   * @return the total amount of the transaction as {@link BigDecimal}
   */
  BigDecimal calculateTotal();
}

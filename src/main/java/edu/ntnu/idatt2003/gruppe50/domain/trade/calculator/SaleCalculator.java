package edu.ntnu.idatt2003.gruppe50.domain.trade.calculator;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

/**
 * Calculates income, commission and tax for selling shares.
 */
public class SaleCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;

  /**
   * Creates a calculator from a share being sold.
   *
   * @param share the share being sold
   * @throws IllegalArgumentException if {@code share} is null
   */
  public SaleCalculator(Share share) {
    Validate.notNull(share, "Share");

    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
    this.salesPrice = share.getStock().getSalesPrice();
  }

  /**
   * Creates a calculator from explicit sale values.
   *
   * @param purchasePrice the original purchase price per share
   * @param salesPrice the current sales price per share
   * @param quantity the number of shares being sold
   */
  public SaleCalculator(BigDecimal purchasePrice, BigDecimal salesPrice, BigDecimal quantity) {
    this.purchasePrice = purchasePrice;
    this.salesPrice = salesPrice;
    this.quantity = quantity;
  }

  /**
   * Calculates the gross amount of the sale before any deductions.
   *
   * @return the gross amount as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateGross() {
    return Money.round(salesPrice.multiply(quantity));
  }

  /**
   * Calculates the commission amount charged by the sale.
   *
   * @return the commission amount as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateCommission() {
    return Money.round(calculateGross().multiply(BigDecimal.valueOf(0.01)));
  }

  /**
   * Calculates the tax applied to the sale of the shares.
   *
   * <p>Validates that the transaction is profitable,
   * and will not tax the player if the player lost money.
   *
   * @return the tax amount as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal profit =
        calculateGross().subtract(calculateCommission()).subtract(purchasePrice.multiply(quantity));

    if (profit.compareTo(BigDecimal.ZERO) > 0) {
      return Money.round(profit.multiply(BigDecimal.valueOf(0.3)));
    } else {
      return BigDecimal.ZERO;
    }
  }

  /**
   * Calculates the total amount won or lost by the sale
   * of the share.
   *
   * <p>The amount the player received by the transaction
   * after deductions such as commission and tax.
   *
   * @return the net amount from the sale as {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateTotal() {
    return Money.round(calculateGross().subtract(calculateCommission()).subtract(calculateTax()));
  }
}

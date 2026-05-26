package edu.ntnu.idatt2003.gruppe50.domain.trade.calculator;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

/**
 * Calculates the cost of purchasing shares.
 */
public class PurchaseCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;

  /**
   * Creates a calculator from a share being purchased.
   *
   * @param share the share being purchased
   * @throws IllegalArgumentException if {@code share} is null
   */
  public PurchaseCalculator(Share share) {
    Validate.notNull(share, "Share");

    purchasePrice = share.getPurchasePrice();
    quantity = share.getQuantity();
  }

  /**
   * Creates a calculator from a purchase price and quantity.
   *
   * @param purchasePrice the purchase price per share
   * @param quantity the number of shares
   * @throws IllegalArgumentException if an argument is null
   */
  public PurchaseCalculator(BigDecimal purchasePrice, BigDecimal quantity) {
    Validate.notNull(purchasePrice, "Purchase price");
    Validate.notNull(quantity, "Quantity");

    this.purchasePrice = purchasePrice;
    this.quantity = quantity;
  }

  /**
   * Calculates the gross amount of the purchase before any fees.
   *
   * @return the gross amount as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateGross() {
    return Money.round(purchasePrice.multiply(quantity));
  }

  /**
   * Calculates the commission charged for the purchase.
   *
   * @return the commission amount as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateCommission() {
    return Money.round(calculateGross().multiply(BigDecimal.valueOf(0.005)));
  }

  /**
   * Returns zero because purchases are not taxed.
   *
   * @return zero
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.valueOf(0);
  }

  /**
   * Calculates the total amount of the purchase of the share.
   *
   * @return the total cost of a purchase as a {@link BigDecimal}
   */
  @Override
  public BigDecimal calculateTotal() {
    return Money.round(calculateGross().add(calculateCommission()).add(calculateTax()));
  }
}

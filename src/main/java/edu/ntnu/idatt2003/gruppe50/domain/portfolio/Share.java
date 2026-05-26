package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a holding of a specific stock.
 *
 * <p>A share consists of a stock, the quantity owned and the purchase price per unit.
 */
public class Share {

  private final UUID shareId;
  private final Stock stock;
  private final BigDecimal quantity;
  private final BigDecimal purchasePrice;
  private final int purchaseWeek;

  /**
   * Creates a new {@code Share} with the given stock, quantity and purchase price.
   *
   * @param stock the stock that is owned
   * @param quantity the number of units owned
   * @param purchasePrice the purchase price per unit
   * @param purchaseWeek the week the share was purchased
   * @throws IllegalArgumentException if any argument is null, zero or negative
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice, int purchaseWeek) {
    shareId = UUID.randomUUID();
    Validate.notNull(stock, "Stock");
    Validate.positive(quantity, "Quantity");
    Validate.positive(purchasePrice, "Purchase price");
    Validate.positiveInt(purchaseWeek, "Purchase week");

    this.stock = stock;
    this.quantity = quantity;
    this.purchasePrice = Money.round(purchasePrice);
    this.purchaseWeek = purchaseWeek;
  }

  /**
   * Returns the Share ID associated with this share
   *
   * @return the share ID
   */
  public UUID getShareId() {
    return shareId;
  }

  /**
   * Returns the stock associated with this share.
   *
   * @return the stock
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the quantity owned of this stock.
   *
   * @return quantity
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   * Returns the purchase price per unit for this stock.
   *
   * @return purchase price
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  /**
   * Returns the week this share was purchased.
   *
   * @return the purchase week
   */
  public int getPurchaseWeek() {
    return purchaseWeek;
  }
}

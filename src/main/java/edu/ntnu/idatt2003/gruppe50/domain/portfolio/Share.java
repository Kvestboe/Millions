package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
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

  /**
   * Creates a new {@code Share} with the given stock, quantity and purchase price.
   *
   * @param stock the stock that is owned
   * @param quantity the number of units owned
   * @param purchasePrice the purchase price per unit
   * @throws IllegalArgumentException if any argument is null, zero or negative.
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    shareId = UUID.randomUUID();
    Validate.notNull(stock, "Stock");
    Validate.positive(quantity, "Quantity");
    Validate.positive(purchasePrice, "Purchase price");

    this.stock = stock;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
  }

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
}

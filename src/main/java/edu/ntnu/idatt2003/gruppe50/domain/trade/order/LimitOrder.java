package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

/**
 * Represents a pending limit order placed by a player on the exchange.
 * A limit order is executed automatically when the market price reaches
 * the player's target price.
 *
 * <p>Subclasses define the trigger condition and execution logic
 * for buy and sell orders respectively.</p>
 */
public abstract class LimitOrder {
  private final Stock stock;
  private final Player player;
  private final BigDecimal targetPrice;
  private final BigDecimal quantity;
  private final int expiryWeek;

  public static final int DEFAULT_DURATION_WEEKS = 6;
  public static final int MAX_DURATION_WEEKS = 12;

  /**
   * Creates a new limit order.
   *
   * @param stock       the stock the order applies to
   * @param player      the player placing the order
   * @param targetPrice the price at which the order should trigger
   * @param quantity    the number of shares
   * @throws NullPointerException     if any reference argument is null
   * @throws IllegalArgumentException if {@code targetPrice} or {@code quantity}
   *                                  is not positive
   */
  protected LimitOrder(Stock stock, Player player, BigDecimal targetPrice, BigDecimal quantity,int currentWeek, int expiryWeek) {
    Validate.notNull(stock, "stock");
    Validate.notNull(player, "player");
    Validate.notNull(targetPrice, "targetPrice");
    Validate.positive(targetPrice, "targetPrice");
    Validate.positive(quantity, "quantity");
    Validate.positiveInt(currentWeek, "Current week");
    Validate.positiveInt(expiryWeek, "Expiry week");
    if (expiryWeek <= currentWeek) {
      throw new IllegalArgumentException(
          "Expiry week must be after current week (current: "
              + currentWeek + ", expiry: " + expiryWeek + ")");
    }
    int duration = expiryWeek - currentWeek;
    if (duration > MAX_DURATION_WEEKS) {
      throw new IllegalArgumentException(
          "Order duration cannot exceed " + MAX_DURATION_WEEKS
              + " weeks (requested: " + duration + ")");
    }

    this.stock = stock;
    this.player = player;
    this.targetPrice = targetPrice;
    this.quantity = quantity;
    this.expiryWeek = expiryWeek;
  }

  public Stock getStock() {
    return stock;
  }

  public Player getPlayer() {
    return player;
  }

  public BigDecimal getTargetPrice() {
    return targetPrice;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public int getExpiryWeek() {
    return expiryWeek;
  }

  public boolean isExpired(int currentWeek) {
    return currentWeek > expiryWeek;
  }

  /**
   * Determines whether this order should be triggered
   * based on the current market price.
   *
   * @param currentPrice the current market price of the stock
   * @return true if the order should be executed, false otherwise
   */
  public abstract boolean shouldTrigger(BigDecimal currentPrice);

  /**
   * Executes the order on the given exchange.
   * Called when {@link #shouldTrigger(BigDecimal)} returns true.
   *
   * @param exchange the exchange on which to execute the order
   */
  public abstract void execute(Exchange exchange);
}
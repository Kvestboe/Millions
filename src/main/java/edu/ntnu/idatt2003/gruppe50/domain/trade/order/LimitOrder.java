package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.LimitOrderDto;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.Map;

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
  private final int currentWeek;

  /**
   * Default number of weeks an order stays active.
   */
  public static final int DEFAULT_DURATION_WEEKS = 6;

  /**
   * Maximum number of weeks an order can stay active.
   */
  public static final int MAX_DURATION_WEEKS = 12;

  /**
   * Recreates a limit order from saved data.
   *
   * @param dto the saved order data
   * @param stockMap stocks mapped by symbol
   * @param player the player who owns the order
   * @return the recreated limit order
   * @throws IllegalArgumentException if the order type is unknown
   */
  public static LimitOrder buildOrder(LimitOrderDto dto, Map<String, Stock> stockMap, Player player) {
    Stock stock = stockMap.get(dto.stockSymbol());
    return switch (dto.type()) {
      case "BUY" -> new LimitBuyOrder(
          stock, player, dto.targetPrice(), dto.quantity(),
          dto.createdWeek(), dto.expiryWeek()
      );
      case "SELL" -> new LimitSellOrder(
          stock, player, dto.targetPrice(), dto.quantity(),
          dto.createdWeek(), dto.expiryWeek()
      );
      case "STOP_LOSS" -> new StopLossOrder(
          stock, player, dto.targetPrice(), dto.quantity(),
          dto.createdWeek(), dto.expiryWeek()
      );
      default -> throw new IllegalArgumentException("Unknown order type: " + dto.type());
    };
  }

  /**
   * Creates a new limit order.
   *
   * @param stock the stock the order applies to
   * @param player the player placing the order
   * @param targetPrice the price that triggers the order
   * @param quantity the number of shares
   * @param currentWeek the week the order was created
   * @param expiryWeek the week the order expires
   * @throws IllegalArgumentException if an argument is invalid
   */
  protected LimitOrder(Stock stock, Player player, BigDecimal targetPrice, BigDecimal quantity, int currentWeek, int expiryWeek) {
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
    this.currentWeek = currentWeek;
  }

  /**
   * Returns the stock this order applies to.
   *
   * @return the stock
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the player who placed the order.
   *
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the target price for the order.
   *
   * @return the target price
   */
  public BigDecimal getTargetPrice() {
    return targetPrice;
  }

  /**
   * Returns the number of shares in the order.
   *
   * @return the quantity
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   * Returns the week the order was created.
   *
   * @return the created week
   */
  public int getCreatedWeek() {
    return currentWeek;
  }

  /**
   * Returns the week the order expires.
   *
   * @return the expiry week
   */
  public int getExpiryWeek() {
    return expiryWeek;
  }

  /**
   * Checks whether the order has expired.
   *
   * @param currentWeek the current week
   * @return true if the order has expired, false otherwise
   */
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

  /**
   * Returns a short display label for the order.
   *
   * @return the order label
   */
  public abstract String label();
}
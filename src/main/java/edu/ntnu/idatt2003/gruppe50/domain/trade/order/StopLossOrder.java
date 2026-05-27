package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import java.math.BigDecimal;

/**
 * Represents a stop-loss order.
 *
 * <p>The order triggers when the stock price falls to or below the target price.
 */
public class StopLossOrder extends LimitOrder {

  /**
   * Creates a new stop-loss order with an explicit expiry week.
   *
   * @param stock       the stock to sell
   * @param player      the player placing the order
   * @param targetPrice the price that triggers the sale
   * @param quantity    the number of shares to sell
   * @param currentWeek the week the order was created
   * @param expiryWeek  the week the order expires
   */
  public StopLossOrder(
      Stock stock,
      Player player,
      BigDecimal targetPrice,
      BigDecimal quantity,
      int currentWeek,
      int expiryWeek
  ) {
    super(stock, player, targetPrice, quantity, currentWeek, expiryWeek);
  }

  /**
   * Creates a new stop-loss order with the default duration.
   */
  public StopLossOrder(
      Stock stock,
      Player player,
      BigDecimal targetPrice,
      BigDecimal quantity,
      int currentWeek
  ) {
    this(stock, player, targetPrice, quantity, currentWeek,
        currentWeek + DEFAULT_DURATION_WEEKS);
  }

  /**
   * Checks whether the stop-loss order should trigger.
   *
   * @param currentPrice the current stock price
   * @return true if the current price is at or below the target price
   */
  @Override
  public boolean shouldTrigger(BigDecimal currentPrice) {
    return currentPrice.compareTo(getTargetPrice()) <= 0;
  }

  /**
   * Executes the stop-loss order on the exchange.
   *
   * @param exchange the exchange to sell through
   */
  @Override
  public void execute(Exchange exchange) {
    exchange.sellQuantity(getStock(), getQuantity(), getPlayer());
  }

  /**
   * Returns a short label for this stop-loss order.
   *
   * @return the label
   */
  @Override
  public String label() {
    return "Stop loss";
  }
}
package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import java.math.BigDecimal;

/**
 * Represents a sell limit order.
 *
 * <p>The order triggers when the current stock price is at or above the target price.
 */
public class LimitSellOrder extends LimitOrder{

  /**
   * Creates a new buy limit order with an explicit expiry week.
   *
   * @param stock the stock to buy
   * @param player the player placing the order
   * @param targetPrice the lowest price the player is willing to sell for
   * @param quantity the number of shares to buy
   * @param currentWeek the week the order was created
   * @param expiryWeek the week the order expires
   */
  public LimitSellOrder(
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
   * Creates a new sell order with the default duration of
   * {@link LimitOrder#DEFAULT_DURATION_WEEKS} weeks.
   */
  public LimitSellOrder(Stock stock, Player player, BigDecimal targetPrice,
                       BigDecimal quantity, int currentWeek) {
    this(stock, player, targetPrice, quantity, currentWeek,
        currentWeek + DEFAULT_DURATION_WEEKS);
  }

  /**
   * Checks whether the sell order should trigger.
   *
   * @param currentPrice the current stock price
   * @return true if the current price is at or above the target price
   */
  @Override
  public boolean shouldTrigger(BigDecimal currentPrice) {
    return getTargetPrice().compareTo(currentPrice) <= 0;
  }

  /**
   * Executes the sell order on the exchange.
   *
   * @param exchange the exchange to sell through
   */
  @Override
  public void execute(Exchange exchange) {
    exchange.sellQuantity(getStock(), getQuantity(), getPlayer());
  }

  /**
   * Returns a short label for this sell order.
   *
   * @return the label
   */
  @Override
  public String label() {
    return "Sell at target price";
  }
}

package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import java.math.BigDecimal;

public class StopLossOrder extends LimitOrder {

  /**
   * Creates a new stop-loss order with an explicit expiry week.
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

  @Override
  public boolean shouldTrigger(BigDecimal currentPrice) {
    return currentPrice.compareTo(getTargetPrice()) <= 0;
  }

  @Override
  public void execute(Exchange exchange) {
    exchange.sellQuantity(getStock(), getQuantity(), getPlayer());
  }

  @Override
  public String label() {
    return "Stop loss";
  }
}
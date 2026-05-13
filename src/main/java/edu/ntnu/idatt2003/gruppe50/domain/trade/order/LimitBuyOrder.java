package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;

import java.math.BigDecimal;

public class LimitBuyOrder extends LimitOrder {

  /**
   * Creates a new buy order with an explicit expiry week.
   */
  public LimitBuyOrder(Stock stock, Player player, BigDecimal targetPrice,
                       BigDecimal quantity, int currentWeek, int expiryWeek) {
    super(stock, player, targetPrice, quantity, currentWeek, expiryWeek);
  }

  /**
   * Creates a new buy order with the default duration of
   * {@link LimitOrder#DEFAULT_DURATION_WEEKS} weeks.
   */
  public LimitBuyOrder(Stock stock, Player player, BigDecimal targetPrice,
                       BigDecimal quantity, int currentWeek) {
    this(stock, player, targetPrice, quantity, currentWeek,
        currentWeek + DEFAULT_DURATION_WEEKS);
  }

  @Override
  public boolean shouldTrigger(BigDecimal currentPrice) {
    return getTargetPrice().compareTo(currentPrice) >= 0;
  }

  @Override
  public void execute(Exchange exchange) {
    exchange.buy(getStock().getSymbol(), getQuantity(), getPlayer());
  }
}

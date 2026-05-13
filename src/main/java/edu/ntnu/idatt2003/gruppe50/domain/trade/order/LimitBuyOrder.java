package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;

import java.math.BigDecimal;

public class LimitBuyOrder extends LimitOrder{

  public LimitBuyOrder(Stock stock, Player player, BigDecimal targetPrice, BigDecimal quantity) {
    super(stock, player, targetPrice, quantity);
  }

  @Override
  public boolean shouldTrigger(BigDecimal currentPrice) {
    return getTargetPrice().compareTo(currentPrice) >= 0;
  }

  @Override
  public void execute(Exchange exchange) {

  }
}

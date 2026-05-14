package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView.OrderType;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView.Side;
import java.math.BigDecimal;

public record DraftOrder(
    Side side,
    OrderType orderType,
    StockDto stock,
    BigDecimal quantity,
    BigDecimal targetPrice,
    Integer duration
) {
  public DraftOrder {
    Validate.notNull(side, "Side");
    Validate.notNull(orderType, "Order type");
    Validate.notNull(stock, "Stock");
    Validate.positive(quantity, "Quantity");

    if (orderType == OrderFormView.OrderType.MARKET) {
      if (targetPrice != null || duration != null) {
        throw new IllegalArgumentException("Market orders cannot have target price or duration");
      }
    } else {
      Validate.positive(targetPrice, "Target Price");
      Validate.positiveInt(duration, "Duration");
    }
  }

  public boolean isPendingOrder() {
    return orderType != OrderFormView.OrderType.MARKET;
  }

  public boolean isLimit() {
    return isPendingOrder();
  }
}
package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

public record DraftOrder(
    OrderSide side,
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

    if (orderType == OrderType.MARKET) {
      if (targetPrice != null || duration != null) {
        throw new IllegalArgumentException("Market orders cannot have target price or duration");
      }
    } else {
      Validate.positive(targetPrice, "Target Price");
      Validate.positiveInt(duration, "Duration");
    }
  }

  public boolean isPendingOrder() {
    return orderType != OrderType.MARKET;
  }

  public boolean isLimit() {
    return isPendingOrder();
  }

  public String label() {
    return getString(side, orderType);
  }

  public static String label(OrderSide side, OrderType type) {
    return getString(side, type);
  }

  private static String getString(OrderSide side, OrderType type) {
    return switch (side) {
      case BUY -> switch (type) {
        case MARKET -> "Buy now";
        case TARGET_PRICE -> "Buy at target price";
        default -> type.toString();
      };
      case SELL -> switch (type) {
        case MARKET -> "Sell now";
        case TARGET_PRICE -> "Sell at target price";
        case STOP_LOSS -> "Stop loss";
      };
    };
  }
}
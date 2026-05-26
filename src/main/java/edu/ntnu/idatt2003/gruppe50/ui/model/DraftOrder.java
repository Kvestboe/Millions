package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

/**
 * UI model for an order before it is submitted.
 *
 * @param side whether the order buys or sells
 * @param orderType type of order to place
 * @param stock stock selected for the order
 * @param quantity order quantity
 * @param targetPrice target or trigger price, or null for market orders
 * @param duration number of weeks the order stays active, or null for market orders
 */
public record DraftOrder(
    OrderSide side,
    OrderType orderType,
    StockDto stock,
    BigDecimal quantity,
    BigDecimal targetPrice,
    Integer duration
) {

  /**
   * Validates draft order invariants after construction.
   *
   * @throws IllegalArgumentException if required fields are missing or invalid
   */
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

  /**
   * Checks whether the order should remain pending until a price trigger is met.
   *
   * @return true for non-market orders, false for market orders
   */
  public boolean isPendingOrder() {
    return orderType != OrderType.MARKET;
  }

  /**
   * Checks whether the order is handled as a limit-style pending order.
   *
   * @return true if the order is pending
   */
  public boolean isLimit() {
    return isPendingOrder();
  }

  /**
   * Returns a display label for this order.
   *
   * @return display label for the order side and type
   */
  public String label() {
    return getString(side, orderType);
  }

  /**
   * Returns a display label for an order side and type.
   *
   * @param side order side
   * @param type order type
   * @return display label
   */
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
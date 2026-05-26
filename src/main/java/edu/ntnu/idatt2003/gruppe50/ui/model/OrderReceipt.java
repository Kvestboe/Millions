package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import java.math.BigDecimal;

/**
 * A receipt describing the outcome of a placed order.
 *
 * <p>An order either executes immediately and produces an
 * {@link ExecutedReceipt}, or it is registered as a pending order
 * and produces a {@link PendingReceipt}.
 */
public interface OrderReceipt {

  /**
   * Receipt for a market order that has already been carried out.
   *
   * @param side whether the order bought or sold shares
   * @param symbol stock symbol involved in the order
   * @param quantity executed quantity
   * @param totalAmount total amount paid or received
   * @param newHoldingQuantity updated owned quantity after execution
   * @param week week when the order executed
   */
  record ExecutedReceipt(
      OrderSide side,
      String symbol,
      BigDecimal quantity,
      BigDecimal totalAmount,
      BigDecimal newHoldingQuantity,
      int week
  ) implements OrderReceipt {}

  /**
   * Receipt for a limit or stop-loss order awaiting a price trigger.
   *
   * @param side whether the order buys or sells shares
   * @param orderType type of pending order
   * @param symbol stock symbol involved in the order
   * @param quantity pending order quantity
   * @param targetPrice target or trigger price
   * @param placedAtWeek week when the order was placed
   * @param expiresAtWeek week when the order expires
   */
  record PendingReceipt(
      OrderSide side,
      OrderType orderType,
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int placedAtWeek,
      int expiresAtWeek
  ) implements OrderReceipt {}
}

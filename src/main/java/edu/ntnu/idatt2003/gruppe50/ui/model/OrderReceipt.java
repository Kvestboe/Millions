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

  /** Receipt for a market order that has already been carried out. */
  record ExecutedReceipt(
      OrderSide side,
      String symbol,
      BigDecimal quantity,
      BigDecimal totalAmount,
      BigDecimal newHoldingQuantity,
      int week
  ) implements OrderReceipt {}

  /** Receipt for a limit or stop-loss order awaiting a price trigger. */
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

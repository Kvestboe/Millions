package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPendingOrdersUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitSellOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.StopLossOrder;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderData;
import java.util.List;
import java.util.UUID;

public class OrdersController {

  private final UUID gameId;
  private final GetPendingOrdersUseCase getPendingOrders;

  public OrdersController(UUID gameId, GetPendingOrdersUseCase getPendingOrders) {
    this.gameId = gameId;
    this.getPendingOrders = getPendingOrders;
  }

  public List<OrderData> getPendingOrders() {
    return getPendingOrders.execute(gameId).stream()
        .map(dto -> new OrderData(
            dto.type(),
            dto.symbol(),
            dto.company(),
            dto.quantity(),
            dto.targetPrice(),
            dto.createdWeek(),
            dto.expiryWeek()
        ))
        .toList();
  }

  private OrderData toOrderData(LimitOrder order) {
    return new OrderData(
        getOrderTypeLabel(order),
        order.getStock().getSymbol(),
        order.getStock().getCompany(),
        order.getQuantity(),
        order.getTargetPrice(),
        order.getCreatedWeek(),
        order.getExpiryWeek()
    );
  }

  private String getOrderTypeLabel(LimitOrder order) {
    if (order instanceof LimitBuyOrder) {
      return "Buy at target price";
    }

    if (order instanceof LimitSellOrder) {
      return "Sell at target price";
    }

    if (order instanceof StopLossOrder) {
      return "Stop loss";
    }

    return "Unknown order";
  }
}

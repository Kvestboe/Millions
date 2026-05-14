package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPendingOrdersUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
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
        .map(this::toOrderData)
        .toList();
  }

  private OrderData toOrderData(LimitOrder order) {
    String type = order instanceof LimitBuyOrder
        ? "Buy limit"
        : "Sell limit";

    return new OrderData(
        type,
        order.getStock().getSymbol(),
        order.getStock().getCompany(),
        order.getQuantity(),
        order.getTargetPrice(),
        order.getCreatedWeek(),
        order.getExpiryWeek()
    );
  }
}

package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.CancelOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPendingOrdersUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrdersController implements Observer {

  private final UUID gameId;
  private final GetPendingOrdersUseCase getPendingOrders;
  private final ObservableList<PendingOrderDto> pendingOrders = FXCollections.observableArrayList();
  private final CancelOrderUseCase cancelOrder;

  public OrdersController(
      UUID gameId,
      GetPendingOrdersUseCase getPendingOrders,
      CancelOrderUseCase cancelOrder,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getPendingOrders = getPendingOrders;
    this.cancelOrder = cancelOrder;
    exchange.addObserver(this);
    refresh();
  }

  public ObservableList<PendingOrderDto> getPendingOrders() {
    return pendingOrders;
  }

  @Override
  public void update() {
    refresh();
  }

  public void refresh() {
    pendingOrders.setAll(getPendingOrders.execute(gameId));
  }

  public void cancel(PendingOrderDto order) {
    cancelOrder.execute(new CancelOrderUseCase.Request(gameId, order));
    refresh();
  }
}

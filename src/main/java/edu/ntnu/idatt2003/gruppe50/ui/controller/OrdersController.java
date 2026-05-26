package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPendingOrdersUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Keeps pending order data synchronized with the exchange state. */
public class OrdersController implements Observer {

  private final UUID gameId;
  private final GetPendingOrdersUseCase getPendingOrders;
  private final ObservableList<PendingOrderDto> pendingOrders = FXCollections.observableArrayList();

  /**
   * Creates a controller for pending orders.
   *
   * @param gameId id of the game session
   * @param getPendingOrders use case used to retrieve pending orders
   * @param exchange observed exchange that triggers order refreshes
   */
  public OrdersController(UUID gameId, GetPendingOrdersUseCase getPendingOrders, Exchange exchange) {
    this.gameId = gameId;
    this.getPendingOrders = getPendingOrders;
    exchange.addObserver(this);
    refresh();
  }

  /**
   * Returns the observable list of pending orders.
   *
   * @return observable pending order DTOs
   */
  public ObservableList<PendingOrderDto> getPendingOrders() {
    return pendingOrders;
  }

  /** Refreshes pending orders when the observed exchange changes. */
  @Override
  public void update() {
    refresh();
  }

  /** Reloads pending orders from the application layer. */
  public void refresh() {
    pendingOrders.setAll(getPendingOrders.execute(gameId));
  }
}

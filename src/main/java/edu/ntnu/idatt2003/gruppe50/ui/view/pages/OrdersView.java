package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class OrdersView extends VBox implements Page {

  private final TableView<PendingOrderDto> table;
  private final OrdersController controller;

  public OrdersView(OrdersController controller) {
    this.controller = controller;

    setSpacing(16);
    getStyleClass().add("orders-page");

    Label title = new Label("Orders");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Pending limit orders");
    subtitle.getStyleClass().add("page-subtitle");

    this.table = createOrderTable();

    getChildren().addAll(title, subtitle, table);
  }

  @Override
  public Parent getView() {
    return this;
  }

  private TableView<PendingOrderDto> createOrderTable() {

    TableView<PendingOrderDto> orderTable = TableFactory.createTable(List.of(
        ColumnPresets.text("Type", PendingOrderDto::type),
        ColumnPresets.text("Stock", PendingOrderDto::symbol),
        ColumnPresets.bigDecimal("Quantity", PendingOrderDto::quantity),
        ColumnPresets.currency("Target price", PendingOrderDto::targetPrice),
        ColumnPresets.integer("Created", PendingOrderDto::createdWeek),
        ColumnPresets.integer("Expires", PendingOrderDto::expiryWeek)
    ));

    orderTable.setItems(controller.getPendingOrders());

    orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    orderTable.setPlaceholder(new Label("No pending orders"));

    return orderTable;
  }
}

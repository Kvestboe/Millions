package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderData;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class OrdersView extends VBox implements Page {

  private final TableView<OrderData> table = new TableView<>();
  private final OrdersController controller;

  public OrdersView(OrdersController controller) {
    this.controller = controller;

    setSpacing(16);
    getStyleClass().add("orders-page");

    Label title = new Label("Orders");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Pending limit orders");
    subtitle.getStyleClass().add("page-subtitle");

    configureTable();

    getChildren().addAll(title, subtitle, table);
  }

  @Override
  public Parent getView() {
    refresh();
    return this;
  }

  private void configureTable() {
    TableColumn<OrderData, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty(data.getValue().type()));

    TableColumn<OrderData, String> stockCol = new TableColumn<>("Stock");
    stockCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty(data.getValue().symbol()));

    TableColumn<OrderData, String> quantityCol = new TableColumn<>("Quantity");
    quantityCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty(data.getValue().quantity().toString()));

    TableColumn<OrderData, String> targetPriceCol = new TableColumn<>("Target price");
    targetPriceCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty(data.getValue().targetPrice() + " kr"));

    TableColumn<OrderData, String> createdWeekCol = new TableColumn<>("Created");
    createdWeekCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty("Week " + data.getValue().createdWeek()));

    TableColumn<OrderData, String> expiryWeekCol = new TableColumn<>("Expires");
    expiryWeekCol.setCellValueFactory(data ->
        new javafx.beans.property.SimpleStringProperty("Week " + data.getValue().expiryWeek()));

    table.getColumns().setAll(
        typeCol,
        stockCol,
        quantityCol,
        targetPriceCol,
        createdWeekCol,
        expiryWeekCol
    );

    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setPlaceholder(new Label("No pending orders"));
  }

  public void setOrders(java.util.List<OrderData> orders) {
    table.setItems(FXCollections.observableArrayList(orders));
  }

  public void refresh() {
    setOrders(controller.getPendingOrders());
  }
}

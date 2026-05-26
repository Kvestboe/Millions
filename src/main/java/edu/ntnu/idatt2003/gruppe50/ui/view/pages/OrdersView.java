package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Page showing the player's pending limit orders.
 *
 * <p>Displays a table of pending {@link PendingOrderDto}
 * entries sorted by creation week (newest first).
 */
public class OrdersView extends VBox implements Page {

  private final TableView<PendingOrderDto> table;
  private final OrdersController controller;

  /**
   * Constructs the orders view.
   *
   * @param controller the controller providing the list of pending orders
   */
  public OrdersView(OrdersController controller) {
    this.controller = controller;

    setSpacing(16);
    getStyleClass().add("orders-page");

    Label title = new Label("Orders");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Pending limit orders");
    subtitle.getStyleClass().add("page-subtitle");

    this.table = createOrderTable();
    table.sort();

    getChildren().addAll(title, subtitle, table);
  }

  /**
   * Returns the root node of the orders page.
   *
   * @return this view
   */
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

    TableColumn<PendingOrderDto, Void> cancelCol = new TableColumn<>("");
    cancelCol.setCellFactory(_ -> new TableCell<>() {
      private final Button cancel = new Button("X");

      {
        setAlignment(Pos.CENTER);

        cancel.getStyleClass().add("system-button-danger");
        cancel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        cancel.setTooltip(new Tooltip("Cancel order"));

        cancel.setOnAction(_ -> {
          PendingOrderDto order = getTableView().getItems().get(getIndex());
          controller.cancel(order);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : cancel);
      }
    });

    orderTable.getColumns().add(cancelCol);

    orderTable.setItems(controller.getPendingOrders());

    orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    orderTable.setPlaceholder(new Label("No pending orders"));

    TableColumn<PendingOrderDto, ?> weekCol = orderTable.getColumns().get(4);
    weekCol.setSortType(TableColumn.SortType.DESCENDING);
    orderTable.getSortOrder().setAll(weekCol);
    return orderTable;
  }
}

package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionType;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.TradingLogCard;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.SearchBarFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ToggleBarFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TransactionsView extends VBox implements Page {

  private static final List<TransactionType> FILTER_VALUES =
      Arrays.asList(null, TransactionType.PURCHASE, TransactionType.SALE);

  private static final double TRADING_LOG_WIDTH = 380;

  private final TransactionQueryController queryController;
  private final TableView<TransactionData> table;
  private final TextField searchField;

  private TransactionType selectedType = null;

  public TransactionsView(
      TransactionQueryController queryController,
      Consumer<TransactionData> onTransactionSelected
  ) {
    this.queryController = queryController;
    this.table = createTransactionTable(onTransactionSelected);
    this.searchField = SearchBarFactory.createSearchField(
        "Search by symbol, company or type...",
        this::applyFilters
    );

    Label title = new Label("Transactions");
    title.getStyleClass().add("page-title");

    TradingLogCard tradingLog = new TradingLogCard(queryController.getTradingLog());
    tradingLog.setMinWidth(TRADING_LOG_WIDTH);
    tradingLog.setPrefWidth(TRADING_LOG_WIDTH);
    tradingLog.setMaxWidth(TRADING_LOG_WIDTH);

    VBox rightColumn = buildRightColumn();

    HBox content = new HBox(16, tradingLog, rightColumn);
    HBox.setHgrow(rightColumn, Priority.ALWAYS);
    VBox.setVgrow(content, Priority.ALWAYS);

    queryController.getTransactions()
        .addListener((ListChangeListener<TransactionData>) _ -> applyFilters());

    getStyleClass().add("transactions-view");
    getStylesheets().add(getClass().getResource("/css/views/transactions.css").toExternalForm());
    getChildren().addAll(title, content);

    applyFilters();
  }

  @Override
  public Parent getView() {
    ScrollPane scroll = new ScrollPane(this);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.getStyleClass().add("app-scroll");
    return scroll;
  }

  private VBox buildRightColumn() {
    Label historyTitle = new Label("History");
    historyTitle.getStyleClass().add("section-title");

    VBox column = new VBox(12, historyTitle, buildFilterToolbar(), table);
    VBox.setVgrow(table, Priority.ALWAYS);
    return column;
  }

  private HBox buildFilterToolbar() {
    HBox segmented = ToggleBarFactory.of(
        List.of("All", "Purchase", "Sell"),
        0,
        i -> {
          selectedType = FILTER_VALUES.get(i);
          applyFilters();
        }
    );

    HBox.setHgrow(searchField, Priority.ALWAYS);

    HBox toolbar = new HBox(searchField, segmented);
    toolbar.getStyleClass().add("filter-toolbar");
    return toolbar;
  }

  private TableView<TransactionData> createTransactionTable(
      Consumer<TransactionData> onTransactionSelected
  ) {
    TableView<TransactionData> t = TableFactory.createTable(List.of(
        ColumnPresets.text("Symbol", x -> x.share().symbol()),
        ColumnPresets.text("Company", x -> x.share().stock()),
        ColumnPresets.text("Type", x -> x.type().name()),
        ColumnPresets.integer("Week", TransactionData::week),
        ColumnPresets.currency("Commission", TransactionData::commissionFee),
        ColumnPresets.currency("Tax", TransactionData::taxFee),
        ColumnPresets.currency("Total", TransactionData::total)
    ));
    t.setPlaceholder(new Label("No transactions yet."));
    t.setOnMousePressed(_ -> {
      TransactionData row = t.getSelectionModel().getSelectedItem();
      if (row != null) {
        onTransactionSelected.accept(row);
      }
    });
    return t;
  }

  private void applyFilters() {
    String query = searchField.getText();
    table.getItems().setAll(queryController.onSearch(query, selectedType));
  }
}

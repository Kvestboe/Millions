package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnDefinition;
import java.math.BigDecimal;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for the market screen, displaying all stocks listed on the exchange.
 *
 * <p>Provides a searchable table where the user can browse stocks and navigate
 * to individual stock detail pages.
 */
public class MarketView implements Page, Observer {

  private final MarketController controller;
  private final TextField searchField;
  private final TableView<Stock> table;
  private final VBox root;

  /**
   * Creates a new MarketView and initializes all UI components.
   *
   * @param controller the controller handling market logic and data retrieval
   */
  public MarketView(MarketController controller) {
    this.controller = controller;
    this.table = createMarketTable();
    this.searchField = createSearchField();
    this.root = createRoot();

    controller.addObserver(this);
  }

  /**
   * Returns the root node of the market screen.
   *
   * @return the root {@link Parent} node
   */
  @Override
  public Parent getView() {
    return root;
  }

  /**
   * Builds and returns the root layout of the market screen. Adds title, search field and stock
   * table to a {@link VBox}.
   *
   * @return a configured {@link VBox} containing all UI components
   */
  private VBox createRoot() {
    Label title = new Label("Market");
    title.getStyleClass().add("market-title");
    VBox box = new VBox(10, title, searchField, table);
    VBox.setVgrow(table, Priority.ALWAYS);
    box.getStyleClass().add("market-root");
    return box;
  }

  /**
   * Creates and configures the search field.
   *
   * <p>Filters the stock table in real time based on symbol or company name.
   *
   * @return a configured {@link TextField}
   */
  private TextField createSearchField() {
    TextField field = new TextField();
    field.setPromptText("Search by symbol or company...");

    field.textProperty().addListener((_, _, newVal) -> {
      if (newVal == null || newVal.isBlank()) {
        table.getItems().setAll(controller.getStocks());
      } else {
        table.getItems().setAll(controller.onSearch(newVal));
      }
    });
    field.setMaxWidth(Double.MAX_VALUE);
    return field;
  }

  /**
   * Creates and configures the market table with columns for symbol, company,
   * price, absolute change and percentage change. Populates the table with
   * stocks from the exchange and registers a click listener for navigation.
   *
   * @return a configured {@link TableView} populated with stocks
   */
  private TableView<Stock> createMarketTable() {
    TableView<Stock> marketTable = createTable(List.of(

        new ColumnDefinition<>("Symbol", s -> new ReadOnlyStringWrapper(s.getSymbol())),
        new ColumnDefinition<>("Company", s -> new ReadOnlyObjectWrapper<>(s.getCompany())),
        new ColumnDefinition<>("Price", s -> new ReadOnlyStringWrapper(MoneyFormat.formatCurrency(s.getSalesPrice()))),

        new ColumnDefinition<>(
            "+/- (kr)",
            s -> new ReadOnlyStringWrapper(MoneyFormat.formatSignedCurrency(s.getLatestPriceChange())),
            (col, row) -> col.setStyle(gainStyle(row.getLatestPriceChange()))
        ),

        new ColumnDefinition<>(
            "+/- (%)",
            s -> new ReadOnlyStringWrapper(MoneyFormat.formatSignedPercent(s.getLatestPriceChangePercent())),
            (col, row) -> col.setStyle(gainStyle(row.getLatestPriceChange()))
        )
    ));
    marketTable.getItems().addAll(controller.getStocks());
    marketTable.setOnMousePressed(_ -> {
      Stock selected = table.getSelectionModel().getSelectedItem();
      if (selected != null) {
        controller.onStockSelected(selected);
      }
    });
    return marketTable;
  }


  private String gainStyle(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) > 0) {
      return "-fx-text-fill: #4CAF50;";
    } else if (value.compareTo(BigDecimal.ZERO) < 0) {
      return "-fx-text-fill: #EF5350;";
    }
    return "-fx-text-fill: #E0E0E0;";
  }
  
    @Override
  public void update() {
    refresh();
  }

  private void refresh() {
    String query = searchField.getText();
    if (query == null || query.isBlank()) {
      table.getItems().setAll(controller.getStocks());
    } else {
      table.getItems().setAll(controller.onSearch(query));
    }
  }
}

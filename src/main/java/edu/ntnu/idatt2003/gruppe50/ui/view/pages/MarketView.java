package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.SearchBarFactory;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
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
public class MarketView extends VBox implements Page {

  private final MarketQueryController queryController;
  private final Consumer<StockDto> onStockSelected;
  private final TextField searchField;
  private final TableView<StockDto> table;
  private final VBox root;

  /**
   * Creates a new MarketView and initializes all UI components.
   *
   * @param queryController the controller handling market logic and data retrieval
   * @param onStockSelected called with the selected stock when a row is clicked
   */
  public MarketView(MarketQueryController queryController, Consumer<StockDto> onStockSelected) {
    this.queryController = queryController;
    this.onStockSelected = onStockSelected;
    table = createMarketTable();
    this.searchField = SearchBarFactory.createSearchField(
        "Search by symbol or company...",
        this::applyFilters
    );
    this.root = createRoot();
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
    box.getStylesheets().add(getClass().getResource("/css/views/market.css").toExternalForm());
    return box;
  }


  /**
   * Creates and configures the market table with columns for symbol, company,
   * price, absolute change and percentage change. Populates the table with
   * stocks from the exchange and registers a click listener for navigation.
   *
   * @return a configured {@link TableView} populated with stocks
   */
  private TableView<StockDto> createMarketTable() {
    TableView<StockDto> marketTable = createTable(List.of(
        ColumnPresets.text("Symbol", StockDto::symbol),
        ColumnPresets.text("Company", StockDto::company),
        ColumnPresets.currency("Price", StockDto::salesPrice),
        ColumnPresets.signedCurrency("+/- (kr)", StockDto::priceChange),
        ColumnPresets.signedPercent("+/- (%)", StockDto::percentChange)
    ));
    marketTable.setItems(queryController.getStocks());
    marketTable.setRowFactory(_ -> {
      TableRow<StockDto> row = new TableRow<>();
      row.setOnMousePressed(_ -> {
        if (!row.isEmpty()) onStockSelected.accept(row.getItem());
      });
      return row;
    });
    return marketTable;
  }

  private void applyFilters() {
    String query = searchField.getText();
    queryController.onSearch(query);
  }
}

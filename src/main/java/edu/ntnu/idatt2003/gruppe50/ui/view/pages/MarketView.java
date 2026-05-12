package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketController;
import java.math.BigDecimal;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * View for the market screen, displaying all stocks listed on the exchange.
 * Provides a searchable table where the user can browse stocks and navigate
 * to individual stock detail pages.
 */
public class MarketView implements Page {

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
    this.table = createStockTable();
    this.searchField = createSearchField();
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
   * Builds and returns the root layout of the market screen.
   * Adds title, search field and stock table to a {@link VBox}.
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
   * Filters the stock table in real time based on symbol or company name.
   *
   * @return a configured {@link TextField}
   */
  private TextField createSearchField() {
    TextField field = new TextField();
    field.setPromptText("Search by symbol or company...");

    field.textProperty().addListener((obs, oldVal, newVal) -> {
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
   * Creates and configures the stock table with columns for symbol, company,
   * price, absolute change and percentage change. Populates the table with
   * stocks from the exchange and registers a click listener for navigation.
   *
   * @return a configured {@link TableView} populated with stocks
   */
  private TableView<Stock> createStockTable() {
    TableView<Stock> table = new TableView<>();

    table.getColumns().addAll(
        createSymbolColumn(),
        createCompanyColumn(),
        createPriceColumn(),
        createChangeColumn(),
        createPercentChangeColumn()
    );

    table.getItems().addAll(controller.getStocks());
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.setMaxWidth(Double.MAX_VALUE);
    table.setMaxHeight(Double.MAX_VALUE);
    table.setOnMouseClicked(event -> {
      Stock selected = table.getSelectionModel().getSelectedItem();
      if (selected != null) {
        controller.onStockSelected(selected);
      }
    });

    return table;
  }

  private TableColumn<Stock, String> createSymbolColumn() {
    TableColumn<Stock, String> col = new TableColumn<>("Symbol");
    col.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getSymbol())
    );
    col.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    return col;
  }

  private TableColumn<Stock, String> createCompanyColumn() {
    TableColumn<Stock, String> col = new TableColumn<>("Company");
    col.setCellValueFactory(data ->
        new SimpleStringProperty(data.getValue().getCompany())
    );
    col.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    return col;
  }

  private TableColumn<Stock, BigDecimal> createPriceColumn() {
    TableColumn<Stock, BigDecimal> col = new TableColumn<>("Price");
    col.setCellValueFactory(data ->
        new SimpleObjectProperty<>(data.getValue().getSalesPrice())
    );
    col.setCellFactory(c -> createMoneyCell());
    col.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    return col;
  }

  private TableCell<Stock, BigDecimal> createMoneyCell() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal value, boolean empty) {
        super.updateItem(value, empty);

        if (empty || value == null) {
          setText(null);
        } else {
          setText(MoneyFormat.formatCurrency(value));
        }
      }
    };
  }

  private TableColumn<Stock, BigDecimal> createChangeColumn() {
    TableColumn<Stock, BigDecimal> col = new TableColumn<>("+/- (kr)");
    col.setCellValueFactory(data ->
        new SimpleObjectProperty<>(data.getValue().getLatestPriceChange())
    );
    col.setCellFactory(c -> createColoredCurrencyCell());
    col.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    return col;
  }

  private TableCell<Stock, BigDecimal> createColoredCurrencyCell() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal value, boolean empty) {
        super.updateItem(value, empty);

        if (empty || value == null) {
          setText(null);
          setStyle("");
        } else {
          setText(MoneyFormat.formatSignedCurrency(value));
          setStyle(gainStyle(value));
        }
      }
    };
  }

  private TableColumn<Stock, BigDecimal> createPercentChangeColumn() {
    TableColumn<Stock, BigDecimal> col = new TableColumn<>("+/- (%)");
    col.setCellValueFactory(data ->
        new SimpleObjectProperty<>(data.getValue().getLatestPriceChangePercent())
    );
    col.setCellFactory(c -> createColoredPercentCell());
    col.setMaxWidth(1f * Integer.MAX_VALUE * 20);
    return col;
  }

  private TableCell<Stock, BigDecimal> createColoredPercentCell() {
    return new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal value, boolean empty) {
        super.updateItem(value, empty);

        if (empty || value == null) {
          setText(null);
          setStyle("");
        } else {
          setText(MoneyFormat.formatSignedPercent(value));
          setStyle(gainStyle(value));
        }
      }
    };
  }

  private String gainStyle(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) > 0) {
      return "-fx-text-fill: #4CAF50;";
    } else if (value.compareTo(BigDecimal.ZERO) < 0) {
      return "-fx-text-fill: #EF5350;";
    }
    return "-fx-text-fill: #E0E0E0;";
  }
}

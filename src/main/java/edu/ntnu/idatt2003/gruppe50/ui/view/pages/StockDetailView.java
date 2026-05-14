package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailController;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class StockDetailView extends VBox implements Page {

  private final StockDto stock;
  private final StockDetailController controller;
  private final Runnable onBack;

  private final Label quantityLabel = new Label();
  private final Label gavLabel = new Label();
  private final Label profitLabel = new Label();
  private final Label profitPercentLabel = new Label();
  private final TextField quantityField = new TextField();
  private final VBox myHoldingBox = new VBox(10);

  public StockDetailView(StockDto stock, StockDetailController controller, Runnable onBack) {
    this.stock = stock;
    this.controller = controller;
    this.onBack = onBack;

    Button backBtn = new Button("Back");
    backBtn.setOnAction(e -> onBack.run());

    getChildren().addAll(
        backBtn,
        createHeader(),
        createStockChart(),
        createStatTiles(),
        createButtonRow());

    refreshHolding();
  }

  @Override
  public Parent getView() {
    return this;
  }

  private VBox createHeader() {
    VBox header = new VBox(10);

    HBox nameBox = new HBox(10,
        new Label(stock.symbol()),
        new Label(stock.company()));

    HBox priceBox = new HBox(10,
        new Label(stock.salesPrice() + " kr"),
        new Label(formatSigned(stock.priceChange()) + " kr"),
        new Label(formatSigned(stock.percentChange()) + "%"));

    header.getChildren().addAll(nameBox, priceBox);
    return header;
  }

  private AreaChart<Number, Number> createStockChart() {
    AreaChartView stockChart = new AreaChartView("Week", "Price");
    stockChart.display("Price development", stock.prices());
    stockChart.getChart().setLegendVisible(false);
    return stockChart.getChart();
  }

  private VBox createStatTiles() {
    VBox myQuantityBox = new VBox(10, new Label("Quantity"), quantityLabel);

    VBox gavBox = new VBox(10, new Label("GAV"), gavLabel);
    VBox profitBox = new VBox(10, new Label("Profit"), profitLabel, profitPercentLabel);

    HBox numbersBox = new HBox(10, myQuantityBox, gavBox, profitBox);

    myHoldingBox.getChildren().addAll(new Label("My holding"), numbersBox);
    return myHoldingBox;
  }

  private HBox createButtonRow() {
    quantityField.setPromptText("Quantity");
    HBox.setHgrow(quantityField, Priority.ALWAYS);

    Button buy = new Button("Buy");
    buy.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(buy, Priority.ALWAYS);
    buy.setOnAction(e -> handleBuy());

    Button sell = new Button("Sell");
    sell.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(sell, Priority.ALWAYS);
    sell.setOnAction(e -> handleSell());

    return new HBox(10, quantityField, buy, sell);
  }

  private void handleBuy() {
    try {
      BigDecimal quantity = new BigDecimal(quantityField.getText().trim());
      controller.buy(stock.symbol(), quantity);
      quantityField.clear();
      refreshHolding();
    } catch (NumberFormatException ex) {
      // TODO: vis feilmelding til bruker
    }
  }

  private void handleSell() {
    Optional<UUID> sold = controller.sellOneOf(stock.symbol());
    if (sold.isEmpty()) {
      // vis melding: "Du eier ingen aksjer av dette selskapet"
      return;
    }
    refreshHolding();
  }



  private void refreshHolding() {
    Optional<ShareData> holding = controller.getHolding(stock.symbol());
    if (holding.isEmpty()) {
      myHoldingBox.setVisible(false);
      myHoldingBox.setManaged(false);
      return;
    }
    myHoldingBox.setVisible(true);
    myHoldingBox.setManaged(true);

    ShareData s = holding.get();
    quantityLabel.setText(s.quantity().toString());
    gavLabel.setText(s.purchasePrice() + " kr");
    quantityLabel.setText(s.quantity().setScale(0, RoundingMode.HALF_UP).toString());
    gavLabel.setText(s.purchasePrice().setScale(2, RoundingMode.HALF_UP) + " kr");

    BigDecimal profit = s.currentShareValue()
        .subtract(s.purchasePrice().multiply(s.quantity()));
    profitLabel.setText(formatSigned(profit) + " kr");
    profitLabel.setText(formatSigned(profit.setScale(2, RoundingMode.HALF_UP)) + " kr");

    BigDecimal percent = s.currentPrice()
        .subtract(s.purchasePrice())
        .divide(s.purchasePrice(), 2, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
    profitPercentLabel.setText(formatSigned(percent) + "%");
    profitPercentLabel.setText(formatSigned(percent.setScale(2, RoundingMode.HALF_UP)) + "%");
  }

  private String formatSigned(BigDecimal value) {
    return (value.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + value;
  }
}

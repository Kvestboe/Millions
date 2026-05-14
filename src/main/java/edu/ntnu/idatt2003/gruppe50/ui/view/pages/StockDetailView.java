package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class StockDetailView extends StackPane implements Page {

  private final StockDto stock;
  private final StockDetailController controller;
  private final Runnable onBack;

  private final Label quantityLabel = new Label();
  private final Label gavLabel = new Label();
  private final Label profitLabel = new Label();
  private final Label profitPercentLabel = new Label();
  private final VBox myHoldingBox = new VBox(10);
  private final VBox content = new VBox(10);
  private final Button sell = new Button("Sell");

  public StockDetailView(StockDto stock, StockDetailController controller, Runnable onBack) {
    this.stock = stock;
    this.controller = controller;
    this.onBack = onBack;

    Button backBtn = new Button("Back");
    backBtn.setOnAction(e -> onBack.run());

    content.getChildren().addAll(
        backBtn,
        createHeader(),
        createStockChart(),
        createStatTiles(),
        createButtonRow());

    getChildren().addAll(content);

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
    Button buy = new Button("Buy");
    buy.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(buy, Priority.ALWAYS);
    buy.setOnAction(e -> handleBuy());

    sell.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(sell, Priority.ALWAYS);
    sell.setOnAction(e -> handleSell());

    return new HBox(10, buy, sell);
  }

  private void handleBuy() {
    OrderFormView popup = new OrderFormView(
        OrderFormView.Side.BUY,
        stock,
        this::closePopup,
        this::handleConfirmedOrder
    );

    getChildren().add(popup);
  }

  private void handleSell() {
    OrderFormView popup = new OrderFormView(
        OrderFormView.Side.SELL,
        stock,
        this::closePopup,
        this::handleConfirmedOrder
    );

    getChildren().add(popup);
  }

  private void closePopup() {
    getChildren().removeIf(node -> node instanceof OrderFormView);
  }

  private void refreshHolding() {
    Optional<ShareDto> holding = controller.getHolding(stock.symbol());
    if (holding.isEmpty()) {
      myHoldingBox.setVisible(false);
      myHoldingBox.setManaged(false);
      sell.setVisible(false);
      sell.setManaged(false);
      return;
    }
    myHoldingBox.setVisible(true);
    myHoldingBox.setManaged(true);
    sell.setVisible(true);
    sell.setManaged(true);

    ShareDto s = holding.get();
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

  private void handleConfirmedOrder(DraftOrder draftOrder) {
    try {
      controller.placeOrder(draftOrder);
      closePopup();
      refreshHolding();
    } catch (RuntimeException ex) {
      System.out.println(ex.getMessage());
    }
  }
}

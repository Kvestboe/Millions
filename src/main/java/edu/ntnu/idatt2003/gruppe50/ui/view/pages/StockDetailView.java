package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrderPlacementController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailQueryController;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.StatCardFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.order.OrderFormView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StockDetailView extends StackPane implements Page {

  private static final double STOCK_DATA_CARD_WIDTH = 380;

  private StockDto stock;
  private final StockDetailQueryController queryController;
  private final OrderPlacementController orderController;
  private final Runnable onBack;
  private final Observer exchangeObserver = this::refresh;

  private final Label priceLabel = new Label();
  private final Label priceChangeLabel = new Label();
  private final Label percentChangeLabel = new Label();

  private final AreaChartView priceChart = new AreaChartView("Week", "Price");
  private final VBox chartBox = new VBox(priceChart.getChart());

  // Holding-felter (uendret)
  private final Label quantityLabel = new Label();
  private final Label gavLabel = new Label();
  private final Label profitLabel = new Label();
  private final Label profitPercentLabel = new Label();
  private final VBox myHoldingBox = new VBox(14);
  private final VBox content = new VBox(10);
  private final Button sell = ButtonFactory.secondary("Sell");

  private final Label errorLabel = new Label();

  public StockDetailView(StockDto stock, StockDetailQueryController queryController,
      OrderPlacementController orderController, Runnable onBack) {
    this.stock = stock;
    this.queryController = queryController;
    this.orderController = orderController;
    this.onBack = onBack;

    Button backBtn = ButtonFactory.secondary("Back", onBack);
    HBox backRow = new HBox(backBtn);

    VBox stockDataCard = createHeader();
    stockDataCard.setMinWidth(STOCK_DATA_CARD_WIDTH);
    stockDataCard.setPrefWidth(STOCK_DATA_CARD_WIDTH);
    stockDataCard.setMaxWidth(STOCK_DATA_CARD_WIDTH);

    VBox rightColumn = new VBox(16, chartBox, createStatTiles(), createButtonRow());
    VBox.setVgrow(chartBox, Priority.ALWAYS);

    HBox splitRow = new HBox(16, stockDataCard, rightColumn);
    HBox.setHgrow(rightColumn, Priority.ALWAYS);
    VBox.setVgrow(splitRow, Priority.ALWAYS);

    content.getChildren().addAll(backRow, splitRow);
    content.getStyleClass().add("stock-detail-view");

    getChildren().addAll(content);

    errorLabel.getStyleClass().add("error-label");
    errorLabel.setVisible(false);

    refresh();

    queryController.subscribe(exchangeObserver);
    sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene == null) {
        queryController.unsubscribe(exchangeObserver);
      }
    });
  }

  @Override
  public Parent getView() {
    return this;
  }

  private VBox createHeader() {
    Label symbol = new Label(stock.symbol());
    symbol.getStyleClass().add("stock-symbol");

    Label company = new Label(stock.company());
    company.getStyleClass().add("stock-company");

    HBox nameBox = new HBox(12, symbol, company);
    nameBox.setAlignment(Pos.CENTER_LEFT);

    VBox priceTile = StatCardFactory.tile("PRICE", priceLabel);
    VBox changeTile = StatCardFactory.tile("CHANGE", priceChangeLabel);
    VBox percentTile = StatCardFactory.tile("CHANGE %", percentChangeLabel);

    priceTile.setMaxWidth(Double.MAX_VALUE);
    changeTile.setMaxWidth(Double.MAX_VALUE);
    percentTile.setMaxWidth(Double.MAX_VALUE);

    VBox priceColumn = new VBox(10, priceTile, changeTile, percentTile);

    VBox header = new VBox(14, nameBox, priceColumn);
    header.getStyleClass().add("card");
    return header;
  }

  private VBox createStatTiles() {
    VBox myQuantityBox = StatCardFactory.tile("QUANTITY", quantityLabel);
    VBox gavBox = StatCardFactory.tile("GAV", gavLabel);
    VBox profitBox = StatCardFactory.tile("PROFIT", profitLabel, profitPercentLabel);

    HBox.setHgrow(myQuantityBox, Priority.ALWAYS);
    HBox.setHgrow(gavBox, Priority.ALWAYS);
    HBox.setHgrow(profitBox, Priority.ALWAYS);

    HBox numbersBox = new HBox(10, myQuantityBox, gavBox, profitBox);

    Label holdingTitle = new Label("My holding");
    holdingTitle.getStyleClass().add("section-title");
    myHoldingBox.getChildren().addAll(holdingTitle, numbersBox);
    myHoldingBox.getStyleClass().add("card");
    return myHoldingBox;
  }

  private HBox createButtonRow() {
    Button buy = ButtonFactory.primary("Buy", this::handleBuy);
    buy.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(buy, Priority.ALWAYS);

    sell.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(sell, Priority.ALWAYS);
    sell.setOnAction(e -> handleSell());

    return new HBox(10, buy, sell);
  }

  private void handleBuy() {
    OrderFormView popup = new OrderFormView(
        queryController.gameId(),
        OrderSide.BUY,
        stock,
        this::closePopup,
        this::handleConfirmedOrder,
        queryController.previewOrderUseCase()
    );

    getChildren().add(popup);
  }

  private void handleSell() {
    OrderFormView popup = new OrderFormView(
        queryController.gameId(),
        OrderSide.SELL,
        stock,
        this::closePopup,
        this::handleConfirmedOrder,
        queryController.previewOrderUseCase()
    );

    getChildren().add(popup);
  }

  private void closePopup() {
    getChildren().removeIf(node -> node instanceof OrderFormView);
  }

  private void refresh() {
    stock = queryController.getStock(stock.symbol()).orElse(stock);
    priceLabel.setText(MoneyFormat.formatCurrency(stock.salesPrice()));
    priceChangeLabel.setText(MoneyFormat.formatSignedCurrency(stock.priceChange()));
    percentChangeLabel.setText(MoneyFormat.formatSignedPercent(stock.percentChange()));

    applySignClass(priceChangeLabel, stock.priceChange());
    applySignClass(percentChangeLabel, stock.percentChange());

    priceChart.display("Price development", stock.prices());
    priceChart.getChart().setLegendVisible(false);
    var markers = queryController.getTransactionMarkers(stock.symbol());
    priceChart.setMarkers(markers.buyWeeks(), markers.sellWeeks(), stock.prices());

    refreshHolding();
  }

  private void applySignClass(Label label, BigDecimal value) {
    label.getStyleClass().removeAll("gain", "loss");
    int sign = value.signum();
    if (sign > 0) label.getStyleClass().add("gain");
    if (sign < 0) label.getStyleClass().add("loss");
  }

  private void refreshHolding() {
    Optional<ShareDto> holding = queryController.getHolding(stock.symbol());
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
    quantityLabel.setText(s.quantity().setScale(0, RoundingMode.HALF_UP).toString());
    gavLabel.setText(MoneyFormat.formatCurrency(s.purchasePrice()));

    BigDecimal profit = s.currentShareValue()
        .subtract(s.purchasePrice().multiply(s.quantity()));
    profitLabel.setText(MoneyFormat.formatSignedCurrency(profit));

    BigDecimal percent = s.currentPrice()
        .subtract(s.purchasePrice())
        .divide(s.purchasePrice(), 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
    profitPercentLabel.setText(MoneyFormat.formatSignedPercent(percent));

    applySignClass(profitLabel, profit);
    applySignClass(profitPercentLabel, percent);
  }

  private void handleConfirmedOrder(DraftOrder draftOrder) {
    try {
      orderController.placeOrder(draftOrder);
      errorLabel.setVisible(false);
      closePopup();
      refresh();
    } catch (RuntimeException ex) {
      errorLabel.setText(ex.getMessage());
      errorLabel.setVisible(true);
    }
  }
}
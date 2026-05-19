package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrderPlacementController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailQueryController;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.order.OrderFormView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StockDetailView extends StackPane implements Page {

  private StockDto stock;
  private final StockDetailQueryController queryController;
  private final OrderPlacementController orderController;
  private final Runnable onBack;
  private final Observer exchangeObserver = this::refresh;

  // Header-felter (oppdateres i refresh)
  private final Label priceLabel = new Label();
  private final Label priceChangeLabel = new Label();
  private final Label percentChangeLabel = new Label();

  // Chart-wrapper så vi kan bytte selve chartet
  private final VBox chartBox = new VBox();

  // Holding-felter (uendret)
  private final Label quantityLabel = new Label();
  private final Label gavLabel = new Label();
  private final Label profitLabel = new Label();
  private final Label profitPercentLabel = new Label();
  private final VBox myHoldingBox = new VBox(10);
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

    content.getChildren().addAll(
        backBtn,
        createHeader(),
        chartBox,
        createStatTiles(),
        createButtonRow());

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
    HBox nameBox = new HBox(10,
        new Label(stock.symbol()),
        new Label(stock.company()));

    HBox priceBox = new HBox(10, priceLabel, priceChangeLabel, percentChangeLabel);

    VBox header = new VBox(10);
    header.getChildren().addAll(nameBox, priceBox);
    return header;
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
    priceLabel.setText(stock.salesPrice() + " kr");
    priceChangeLabel.setText(formatSigned(stock.priceChange()) + " kr");
    percentChangeLabel.setText(formatSigned(stock.percentChange()) + "%");

    AreaChartView chart = new AreaChartView("Week", "Price");
    chart.display("Price development", stock.prices());
    chart.getChart().setLegendVisible(false);
    chartBox.getChildren().setAll(chart.getChart());

    refreshHolding();
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
    gavLabel.setText(s.purchasePrice().setScale(2, RoundingMode.HALF_UP) + " kr");

    BigDecimal profit = s.currentShareValue()
        .subtract(s.purchasePrice().multiply(s.quantity()));
    profitLabel.setText(formatSigned(profit.setScale(2, RoundingMode.HALF_UP)) + " kr");

    BigDecimal percent = s.currentPrice()
        .subtract(s.purchasePrice())
        .divide(s.purchasePrice(), 2, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
    profitPercentLabel.setText(formatSigned(percent.setScale(2, RoundingMode.HALF_UP)) + "%");
  }

  private String formatSigned(BigDecimal value) {
    return (value.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + value;
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
package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItem;
import edu.ntnu.idatt2003.gruppe50.domain.shop.items.ThemeItem;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.ShopController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View for the shop page where players can buy coins and themes.
 */
public class ShopView extends ScrollPane implements Page {

  private static final double THEME_CARD_GAP = 12;
  private static final int THEME_CARDS_PER_ROW = 3;

  private final ShopController controller;
  private final VBox content = new VBox();
  private final Label coinsLabel = new Label();
  private final Label moneyLabel = new Label();
  private final Label messageLabel = new Label();
  private final Label buyPreviewLabel = new Label();
  private final TextField coinAmountField = new TextField("10");
  private final FlowPane themesGrid = new FlowPane();
  private final Consumer<String> onThemeChanged;
  private final Runnable onPlayerBalanceChanged;
  private final Label rateLabel = new Label();
  private final AreaChartView coinPriceChart = new AreaChartView("Week", "Coin price");

  /**
   * Creates a shop view connected to the given controller.
   *
   * @param controller the shop controller used by the view
   */
  public ShopView(ShopController controller, Consumer<String> onThemeChanged,
                  Runnable onPlayerBalanceChanged) {
    this.controller = controller;
    this.onThemeChanged = onThemeChanged;
    this.onPlayerBalanceChanged = onPlayerBalanceChanged;
    build();
    refresh();
  }

  /**
   * Returns this page's JavaFX view node.
   *
   * @return the shop view
   */
  @Override
  public Parent getView() {
    refresh();
    return this;
  }

  private void build() {
    getStyleClass().add("shop-scroll");
    setFitToWidth(true);
    setFitToHeight(false);
    setHbarPolicy(ScrollBarPolicy.NEVER);
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

    content.getStyleClass().add("shop-view");
    content.setSpacing(16);
    content.setPadding(new Insets(32));
    content.setMaxWidth(980);

    StackPane wrapper = new StackPane(content);
    wrapper.getStyleClass().add("shop-wrapper");
    wrapper.setAlignment(Pos.TOP_CENTER);
    wrapper.prefWidthProperty().bind(widthProperty());

    setContent(wrapper);

    configureThemesGrid();

    content.getChildren().addAll(
        createHeader(),
        createSectionLabel("COIN EXCHANGE"),
        createCoinExchangeSection(),
        createSectionLabel("THEMES"),
        themesGrid,
        messageLabel
    );

    messageLabel.getStyleClass().add("shop-message");
    buildThemeCards();
  }

  private void configureThemesGrid() {
    themesGrid.getStyleClass().add("theme-grid");
    themesGrid.setHgap(THEME_CARD_GAP);
    themesGrid.setVgap(THEME_CARD_GAP);
    themesGrid.setPrefWrapLength(980);
    themesGrid.setMaxWidth(Double.MAX_VALUE);
  }

  private HBox createHeader() {
    Label title = new Label("Shop");
    title.getStyleClass().add("page-title");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    coinsLabel.getStyleClass().add("shop-coin-badge");

    HBox header = new HBox(12, title, spacer, coinsLabel);
    header.setAlignment(Pos.CENTER_LEFT);
    return header;
  }

  private Label createSectionLabel(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("shop-section-label");
    return label;
  }

  private HBox createCoinExchangeSection() {
    Label rateCaption = new Label("CURRENT RATE");
    rateCaption.getStyleClass().add("shop-caption");

    rateLabel.getStyleClass().add("shop-rate");

    moneyLabel.getStyleClass().add("shop-balance");
    buyPreviewLabel.getStyleClass().add("shop-balance");

    coinAmountField.getStyleClass().add("shop-coin-input");
    coinAmountField.setPrefWidth(120);
    coinAmountField.textProperty().addListener(
        (observable, oldValue, newValue) -> updateBuyPreview()
    );

    Button buyButton = ButtonFactory.styled("", "shop-buy-button", this::buyCoins);
    buyButton.textProperty().bind(buyPreviewLabel.textProperty());

    HBox buyRow = new HBox(8, coinAmountField, buyButton);
    buyRow.setAlignment(Pos.CENTER_LEFT);

    VBox rateBox = new VBox(6, rateCaption, rateLabel, buyRow, moneyLabel);

    AreaChart<Number, Number> chart = createCoinPriceChart();
    HBox.setHgrow(chart, Priority.ALWAYS);

    HBox section = new HBox(28, rateBox, chart);
    section.getStyleClass().add("coin-exchange-panel");
    section.setAlignment(Pos.CENTER_LEFT);
    section.setMaxWidth(Double.MAX_VALUE);

    return section;
  }

  private void buildThemeCards() {
    themesGrid.getChildren().clear();

    for (ShopItem item : controller.getItems()) {
      themesGrid.getChildren().add(createThemeCard(item));
    }
  }

  private VBox createThemeCard(ShopItem item) {
    VBox card = new VBox(6);
    card.getStyleClass().add("theme-card");

    card.prefWidthProperty().bind(
        themesGrid.widthProperty()
            .subtract(THEME_CARD_GAP * THEME_CARDS_PER_ROW)
            .divide(THEME_CARDS_PER_ROW)
    );

    card.setMinWidth(0);
    card.setMaxWidth(Double.MAX_VALUE);
    card.setMinHeight(160);

    Label title = new Label(item.getName());
    title.getStyleClass().add("theme-card-title");

    Label description = new Label(item.getDescription());
    description.getStyleClass().add("theme-card-description");
    description.setWrapText(true);

    Label meta = new Label(createThemeMetaText(item));
    meta.getStyleClass().add("theme-card-meta");

    Button button = ButtonFactory.styled(
        createThemeButtonText(item), "theme-buy-button", () -> purchaseItem(item));
    button.setDisable(isActiveTheme(item));

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    card.getChildren().addAll(title, description, spacer, meta, button);
    return card;
  }

  private String createThemeMetaText(ShopItem item) {
    String priceText = item.getPrice(controller.getDifficulty()) + " coins";

    if (item.getRequiredNetWorth().compareTo(BigDecimal.ZERO) == 0) {
      return priceText;
    }

    return priceText + " | requires "
        + MoneyFormat.formatCurrency(item.getRequiredNetWorth())
        + " net worth";
  }

  private String createThemeButtonText(ShopItem item) {
    if (isActiveTheme(item)) {
      return "Active";
    }

    if (item instanceof ThemeItem themeItem && controller.ownsTheme(themeItem.getThemeId())) {
      return "Use";
    }

    return "Buy";
  }

  private boolean isActiveTheme(ShopItem item) {
    if (item instanceof ThemeItem themeItem) {
      return controller.getActiveTheme().equals(themeItem.getThemeId());
    }

    return false;
  }

  private void buyCoins() {
    int coins;

    try {
      coins = Integer.parseInt(coinAmountField.getText().trim());
    } catch (NumberFormatException e) {
      messageLabel.setText("Coin amount must be a whole number.");
      return;
    }

    messageLabel.setText(controller.buyCoins(coins));
    onPlayerBalanceChanged.run();
    refresh();
  }

  private void purchaseItem(ShopItem item) {
    String previousTheme = controller.getActiveTheme();

    messageLabel.setText(controller.purchaseItem(item.getId()));

    if (!previousTheme.equals(controller.getActiveTheme())) {
      onThemeChanged.accept(controller.getActiveTheme());
    }

    refresh();
  }

  /**
   * Refreshes the coin balance, cash balance, current coin rate, the coin
   * price chart and the theme cards. Should be called after any change
   * that affects the player's money or coin holdings.
   */
  public void refresh() {
    coinsLabel.setText(controller.getPlayerCoins() + " coins");
    moneyLabel.setText(
        "Current balance: " + MoneyFormat.formatCurrency(controller.getPlayerMoney()));
    rateLabel.setText(MoneyFormat.format(controller.getCurrentCoinPrice()) + " kr / coin");
    coinPriceChart.display("Coin price history", controller.getCoinPriceHistory());
    updateBuyPreview();
    buildThemeCards();
  }

  private void updateBuyPreview() {
    try {
      int coins = Integer.parseInt(coinAmountField.getText().trim());

      if (coins <= 0) {
        buyPreviewLabel.setText("Buy coins");
        return;
      }

      BigDecimal total = controller.getCurrentCoinPrice()
          .multiply(BigDecimal.valueOf(coins))
          .setScale(2, RoundingMode.HALF_UP);

      buyPreviewLabel.setText("Buy " + coins + " coins - " + MoneyFormat.formatCurrency(total));
    } catch (NumberFormatException e) {
      buyPreviewLabel.setText("Buy coins");
    }
  }

  private AreaChart<Number, Number> createCoinPriceChart() {
    coinPriceChart.display("Coin price history", controller.getCoinPriceHistory());

    AreaChart<Number, Number> chart = coinPriceChart.getChart();
    chart.setLegendVisible(false);
    chart.setMinHeight(180);
    chart.setPrefHeight(180);
    chart.setMaxHeight(220);
    chart.setMinWidth(360);

    return chart;
  }
}
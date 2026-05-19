package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.view.WindowConfig;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekHolding;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekSummary;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.StatCardFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.WeekSummaryPopup;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private final Runnable onMainMenu;
  private final Runnable onPlayAgain;
  private final Consumer<String> onThemeChanged;
  private NavigationManager navManager;
  private BorderPane root;
  private ShopView shopView;
  private StackPane popupHost;   // ← ny linje

  private HBox bottomBar;
  private Label weekValue;
  private Label netWorthValue;
  private Label deltaValue;
  private Label cashValue;
  private Button advanceButton;

  private BigDecimal lastWeeklyDelta = BigDecimal.ZERO;
  private static final BigDecimal DANGER_ZONE_MULTIPLIER = new BigDecimal("1.15");

  public GameViewCoordinator(
      GameSessionControllerBundle bundle,
      Runnable onMainMenu,
      Runnable onPlayAgain,
      Consumer<String> onThemeChanged
  ) {
    this.bundle = bundle;
    this.onMainMenu = onMainMenu;
    this.onPlayAgain = onPlayAgain;
    this.onThemeChanged = onThemeChanged;
  }

  public Scene getScene() {
    navManager = new NavigationManager(buildPages());
    NavBar navBar = new NavBar(navManager::navigateTo);

    root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());
    bottomBar = buildBottomBar();
    root.setBottom(bottomBar);
    refreshBottomBar();
    bundle.session().getExchange().addObserver(this::refreshBottomBar);

    navManager.navigateTo(PageId.DASHBOARD);
    bundle.market().setOnStockSelected(this::navigateToStockDetail);

    bundle.game().setOutcomeListener(outcome -> {
      GameSession session = bundle.session();
      BigDecimal finalNetWorth = session.getPlayer().getNetWorth();
      int weeksPlayed = session.getExchange().getWeek();
      Difficulty difficulty = session.getDifficulty();

      root.setTop(null);
      root.setBottom(null);
      navManager.show(new GameOverView(
          outcome, finalNetWorth, weeksPlayed, difficulty,
          onPlayAgain, onMainMenu
      ));
    });

    popupHost = new StackPane(root);
    Scene scene = new Scene(popupHost, WindowConfig.WIDTH, WindowConfig.HEIGHT);
    return scene;
  }

  private Map<PageId, Page> buildPages() {
    Map<PageId, Page> pages = new EnumMap<>(PageId.class);

    pages.put(PageId.DASHBOARD, new DashboardView(bundle.game()));
    pages.put(PageId.MARKET, new MarketView(bundle.market(), this::navigateToStockDetail));
    pages.put(PageId.PORTFOLIO, new PortfolioView(bundle.portfolio(), bundle.game()));
    pages.put(PageId.SHOP, shopView = new ShopView(bundle.shop(), onThemeChanged, bundle.portfolio()::update));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(bundle.transactions()));
    pages.put(PageId.ORDERS, new OrdersView(bundle.ordersController()));

    return pages;
  }

  private void navigateToStockDetail(StockDto stock) {
    StockDetailView view = new StockDetailView(
        stock,
        bundle.stockQuery(),
        bundle.orderPlacement(),
        () -> navManager.navigateTo(PageId.MARKET));
    navManager.show(view);
  }

  private HBox buildBottomBar() {
    weekValue     = new Label();
    netWorthValue = new Label();
    deltaValue    = new Label();
    cashValue     = new Label();

    VBox week = StatCardFactory.compact("Week", weekValue, "bottom-bar-value");
    VBox netWorth = StatCardFactory.compact("Net worth", netWorthValue, "bottom-bar-value-gold");
    VBox delta = StatCardFactory.compact("This week", deltaValue, "bottom-bar-value");
    VBox cash = StatCardFactory.compact("Cash", cashValue, "bottom-bar-value");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    advanceButton = ButtonFactory.styled("", "advance-button", this::onAdvanceWeek);

    HBox bar = new HBox(week, netWorth, delta, cash, spacer, advanceButton);
    bar.getStyleClass().add("bottom-bar");
    return bar;
  }

  private void onAdvanceWeek() {
    GameSession session = bundle.session();
    int prevWeek = session.getExchange().getWeek();
    BigDecimal before = session.getPlayer().getNetWorth();

    bundle.game().advanceWeek();

    if (session.getState() == GameSessionState.FINISHED) {
      refreshBottomBar();
      return;
    }

    BigDecimal after = session.getPlayer().getNetWorth();
    lastWeeklyDelta = after.subtract(before);

    bundle.shop().advanceCoinExchange();
    if (shopView != null) {
      shopView.refresh();
    }

    refreshBottomBar();

    WeekSummary summary = buildWeekSummary(prevWeek, before, after);

    Node[] holder = new Node[1];
    WeekSummaryPopup popup = new WeekSummaryPopup(summary, () -> closePopup(holder[0]));
    holder[0] = popup;
    showPopup(popup);
  }

  private WeekSummary buildWeekSummary(int prevWeek, BigDecimal before, BigDecimal after) {
    Player player = bundle.session().getPlayer();

    List<WeekHolding> holdings = player.getPortfolio().getShares().stream()
        .collect(Collectors.groupingBy(s -> s.getStock().getSymbol()))
        .entrySet().stream()
        .map(e -> {
          Stock stock = e.getValue().get(0).getStock();
          BigDecimal qty = e.getValue().stream()
              .map(Share::getQuantity)
              .reduce(BigDecimal.ZERO, BigDecimal::add);
          BigDecimal delta = stock.getLatestPriceChange().multiply(qty);
          BigDecimal percent = stock.getLatestPriceChangePercent();
          return new WeekHolding(stock.getSymbol(), qty, delta, percent);
        })
        .sorted((a, b) -> b.weeklyDelta().abs().compareTo(a.weeklyDelta().abs()))
        .toList();

    return new WeekSummary(
        prevWeek,
        bundle.session().getExchange().getWeek(),
        before, after,
        player.getMoney(),
        holdings,
        List.of(), List.of()
    );
  }

  private void refreshBottomBar() {
    Player player         = bundle.session().getPlayer();
    Exchange exchange     = bundle.session().getExchange();
    Difficulty difficulty = bundle.session().getDifficulty();

    BigDecimal netWorth = player.getNetWorth();
    BigDecimal threshold = player.getStartingMoney()
        .multiply(BigDecimal.valueOf(difficulty.getGameOverThreshold()));
    BigDecimal warningBand = threshold.multiply(DANGER_ZONE_MULTIPLIER);
    boolean danger = netWorth.compareTo(warningBand) < 0;

    weekValue.setText("Week " + exchange.getWeek());
    netWorthValue.setText(MoneyFormat.formatCurrency(netWorth));

    deltaValue.setText(MoneyFormat.formatSignedCurrency(lastWeeklyDelta));
    deltaValue.getStyleClass().removeAll("gain", "loss");
    if (lastWeeklyDelta.signum() > 0) deltaValue.getStyleClass().add("gain");
    if (lastWeeklyDelta.signum() < 0) deltaValue.getStyleClass().add("loss");

    if (danger) {
      cashValue.setText("Game over at " + MoneyFormat.formatCurrency(threshold));
    } else {
      cashValue.setText(MoneyFormat.formatCurrency(player.getMoney()));
    }

    advanceButton.setText("Advance to Week " + (exchange.getWeek() + 1) + " →");

    bottomBar.getStyleClass().remove("bottom-bar-danger");
    if (danger) bottomBar.getStyleClass().add("bottom-bar-danger");
  }

  private void showPopup(Node popup) {
    popupHost.getChildren().add(popup);
  }

  private void closePopup(Node popup) {
    popupHost.getChildren().remove(popup);
  }
}

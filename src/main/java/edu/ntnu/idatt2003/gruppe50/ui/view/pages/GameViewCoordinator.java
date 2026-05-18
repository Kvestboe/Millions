package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private final Runnable onMainMenu;
  private final Runnable onPlayAgain;
  private NavigationManager navManager;
  private BorderPane root;

  private HBox bottomBar;
  private Label weekValue;
  private Label netWorthValue;
  private Label deltaValue;
  private Label cashValue;
  private Button advanceButton;

  private BigDecimal netWorthBeforeAdvance;
  private BigDecimal lastWeeklyDelta = BigDecimal.ZERO;
  private static final BigDecimal DANGER_ZONE_MULTIPLIER = new BigDecimal("1.15");

  public GameViewCoordinator(GameSessionControllerBundle bundle, Runnable onMainMenu, Runnable onPlayAgain) {
    this.bundle = bundle;
    this.onMainMenu = onMainMenu;
    this.onPlayAgain = onPlayAgain;
  }

  public Scene getScene() {
    navManager = new NavigationManager(buildPages());
    NavBar navBar = new NavBar(navManager::navigateTo);

    root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());
    bottomBar = buildBottomBar();
    root.setBottom(bottomBar);
    refreshBottomBar();                          // initial render
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

    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    return scene;
  }

  private Map<PageId, Page> buildPages() {
    Map<PageId, Page> pages = new EnumMap<>(PageId.class);

    pages.put(PageId.DASHBOARD, new DashboardView(bundle.game()));
    pages.put(PageId.MARKET, new MarketView(bundle.market()));
    pages.put(PageId.PORTFOLIO, new PortfolioView(bundle.portfolio(), bundle.game()));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(bundle.transactions()));
    pages.put(PageId.ORDERS, new OrdersView(bundle.ordersController()));

    return pages;
  }

  private void navigateToStockDetail(StockDto stock) {
    StockDetailView view = new StockDetailView(
        stock, bundle.stockDetail(), () -> navManager.navigateTo(PageId.MARKET));
    navManager.show(view);
  }

  private HBox buildBottomBar() {
    weekValue     = new Label();
    netWorthValue = new Label();
    deltaValue    = new Label();
    cashValue     = new Label();

    HBox week     = stat("Week",       weekValue,     "bottom-bar-value");
    HBox netWorth = stat("Net worth",  netWorthValue, "bottom-bar-value-gold");
    HBox delta    = stat("This week",  deltaValue,    "bottom-bar-value");
    HBox cash     = stat("Cash",       cashValue,     "bottom-bar-value");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    advanceButton = new Button();
    advanceButton.getStyleClass().add("advance-button");
    advanceButton.setOnAction(_ -> onAdvanceWeek());

    HBox bar = new HBox(week, netWorth, delta, cash, spacer, advanceButton);
    bar.getStyleClass().add("bottom-bar");
    return bar;
  }

  private HBox stat(String label, Label value, String valueClass) {
    Label l = new Label(label.toUpperCase());
    l.getStyleClass().add("bottom-bar-label");
    value.getStyleClass().add(valueClass);

    VBox box = new VBox(l, value);
    box.getStyleClass().add("bottom-bar-stat");
    return new HBox(box);
  }

  private void onAdvanceWeek() {
    BigDecimal before = bundle.session().getPlayer().getNetWorth();
    bundle.game().advanceWeek();
    BigDecimal after = bundle.session().getPlayer().getNetWorth();
    lastWeeklyDelta = after.subtract(before);
    refreshBottomBar();
    // TODO: åpne weekly-summary popup her senere
  }

  private void refreshBottomBar() {
    Player player        = bundle.session().getPlayer();
    Exchange exchange    = bundle.session().getExchange();
    Difficulty difficulty = bundle.session().getDifficulty();

    BigDecimal netWorth   = player.getNetWorth();
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
}

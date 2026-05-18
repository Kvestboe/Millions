package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private final Runnable onMainMenu;
  private final Runnable onPlayAgain;
  private NavigationManager navManager;
  private BorderPane root;
  private final Consumer<String> onThemeChanged;

  public GameViewCoordinator(GameSessionControllerBundle bundle, Runnable onMainMenu, Runnable onPlayAgain, Consumer<String> onThemeChanged) {
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
    root.setBottom(buildBottomBar());

    navManager.navigateTo(PageId.DASHBOARD);
    bundle.market().setOnStockSelected(this::navigateToStockDetail);

    bundle.game().setOutcomeListener(outcome -> {
      GameSession session = bundle.session();
      BigDecimal finalNetWorth = session.getPlayer().getNetWorth();
      int weeksPlayed = session.getExchange().getWeek();
      Difficulty difficulty = session.getDifficulty();

      root.setTop(null); // skjul navbar
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
    pages.put(PageId.SHOP, new ShopView(bundle.shop(), onThemeChanged));
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
    GameSession session = bundle.session();

    Label weekLabel = new Label("Week " + session.getExchange().getWeek());
    Label netWorthLabel = new Label(
        MoneyFormat.formatCurrency(session.getPlayer().getNetWorth()));

    weekLabel.getStyleClass().add("label-muted");
    netWorthLabel.getStyleClass().add("field-label");

    Button advanceBtn = new Button("Advance to Week "
        + (session.getExchange().getWeek() + 1) + " →");
    advanceBtn.getStyleClass().add("advance-button");
    advanceBtn.setOnAction(_ -> onAdvanceWeek());

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox bar = new HBox(24, weekLabel, netWorthLabel, spacer, advanceBtn);
    bar.getStyleClass().add("bottom-bar");
    advanceBtn.setPrefWidth(200);
    bar.setMaxHeight(20);
    bar.setAlignment(Pos.CENTER_LEFT);
    bar.setPadding(new Insets(12, 32, 16, 32));
    return bar;
  }

  private void onAdvanceWeek() {
    bundle.game().advanceWeek();
    // TODO: åpne weekly summary popup her
  }
}

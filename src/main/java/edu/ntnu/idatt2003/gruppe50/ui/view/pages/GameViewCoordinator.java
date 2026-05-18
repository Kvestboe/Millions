package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameResult;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private final Runnable onMainMenu;
  private final Runnable onPlayAgain;
  private NavigationManager navManager;
  private BorderPane root;

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

    navManager.navigateTo(PageId.DASHBOARD);
    bundle.market().setOnStockSelected(this::navigateToStockDetail);

    // ui/view/pages/GameViewCoordinator.java
    bundle.game().setOutcomeListener(outcome -> {
      GameSession session = bundle.session();
      GameResult result = new GameResult(
          outcome == GameOutcome.WON,
          session.getPlayer().getNetWorth(),
          session.getPlayer().getStartingMoney(),
          session.getExchange().getWeek(),
          session.getDifficulty()
      );

      root.setTop(null);
      navManager.show(new GameOverView(result, onPlayAgain, onMainMenu));
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
}

package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.*;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameViewCoordinator {
  private final GameController gameController;
  private final PortfolioQueryController portfolioQueryController;
  private final DashboardQueryController dashboardQueryController = null;
  private final TransactionQueryController transactionQueryController;
  private final MarketController marketController;

  public GameViewCoordinator(
      GameController gameController,
      PortfolioQueryController portfolioQueryController,
      MarketController marketController,
      TransactionQueryController transactionQueryController
  ) {
    this.gameController = gameController;
    this.portfolioQueryController = portfolioQueryController;
    this.marketController = marketController;
    this.transactionQueryController = transactionQueryController;
  }

  public Scene getScene() {
    NavigationManager navManager = new NavigationManager(buildPages());
    NavBar navBar = new NavBar(navManager::navigateTo);

    BorderPane root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());

    navManager.navigateTo(PageId.DASHBOARD);

    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(
        getClass().getResource("/css/styles.css").toExternalForm()
    );
    return scene;
  }

  private Map<PageId, Page> buildPages() {
    Map<PageId, Page> pages = new EnumMap<>(PageId.class);

    pages.put(PageId.DASHBOARD, new DashboardView(dashboardQueryController));
    pages.put(PageId.MARKET, new MarketView(marketController));
    pages.put(PageId.PORTFOLIO, new PortfolioView(portfolioQueryController, gameController));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(transactionQueryController));

    return pages;
  }
}

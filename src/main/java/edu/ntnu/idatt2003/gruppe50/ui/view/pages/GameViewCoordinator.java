package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
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
  private final MarketController marketController;

  public GameViewCoordinator(
      GameController gameController,
      PortfolioQueryController portfolioQueryController,
      MarketController marketController
  ) {
    this.gameController = gameController;
    this.portfolioQueryController = portfolioQueryController;
    this.marketController = marketController;
  }

  public Scene getScene() {
    NavigationManager navManager = new NavigationManager(buildPages());
    NavBar navBar = new NavBar(navManager::navigateTo);

    BorderPane root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());

    navManager.navigateTo(PageId.DASHBOARD);

    return new Scene(root, 600, 400);
  }

  private Map<PageId, Page> buildPages() {
    Map<PageId, Page> pages = new EnumMap<>(PageId.class);

    pages.put(PageId.DASHBOARD, new DashBoardView());
    pages.put(PageId.MARKET, new MarketView(marketController));
    pages.put(PageId.PORTFOLIO, new PortfolioView(portfolioQueryController, gameController));
    pages.put(PageId.TRANSACTIONS, new TransactionsView());

    return pages;
  }
}

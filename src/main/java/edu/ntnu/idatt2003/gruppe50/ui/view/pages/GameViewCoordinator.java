package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.*;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameViewCoordinator {
  private final GameController gameController;
  private final PortfolioQueryController portfolioQueryController;
  private final BuyShareUseCase buyShare;
  private final UUID gameId;
  private final Exchange exchange;
  private final Player player;
  private NavigationManager navManager;

  public GameViewCoordinator(GameController gameController,
                             PortfolioQueryController portfolioQueryController,
                             BuyShareUseCase buyShare,
                             UUID gameId, Exchange exchange, Player player) {
    this.exchange = exchange;
    this.player = player;
    this.gameController = gameController;
    this.portfolioQueryController = portfolioQueryController;
    this.buyShare = buyShare;
    this.gameId = gameId;
  }

  public Scene getScene() {
    this.navManager = new NavigationManager(buildPages());
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
    MarketController marketController = new MarketController(
        exchange,
        player,
        this::navigateToStockDetail
    );

    pages.put(PageId.DASHBOARD, new DashboardView(dashboardQueryController));
    pages.put(PageId.MARKET, new MarketView(marketController));
    pages.put(PageId.PORTFOLIO, new PortfolioView(portfolioQueryController, gameController));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(transactionQueryController));

    return pages;
  }

  private void navigateToStockDetail(Stock stock) {
    StockDetailController controller = new StockDetailController(gameId, buyShare);
    StockDetailView view = new StockDetailView(stock, controller);
    navManager.show(view);
  }
}
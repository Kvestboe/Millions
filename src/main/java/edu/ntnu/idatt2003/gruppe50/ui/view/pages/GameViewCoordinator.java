package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
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
  private final TransactionQueryController transactionQueryController;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final UUID gameId;
  private final Exchange exchange;
  private final Player player;
  private NavigationManager navManager;
  private final GetPortfolioUseCase getPortfolio;

  public GameViewCoordinator(GameController gameController,
                             PortfolioQueryController portfolioQueryController, TransactionQueryController transactionQueryController,
                             BuyShareUseCase buyShare, SellShareUseCase sellShare,
                             UUID gameId, Exchange exchange, Player player, GetPortfolioUseCase getPortfolio) {
    this.transactionQueryController = transactionQueryController;
    this.sellShare = sellShare;
    this.exchange = exchange;
    this.player = player;
    this.gameController = gameController;
    this.portfolioQueryController = portfolioQueryController;
    this.buyShare = buyShare;
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
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

    pages.put(PageId.DASHBOARD, new DashboardView(gameController));
    pages.put(PageId.MARKET, new MarketView(marketController));
    pages.put(PageId.PORTFOLIO, new PortfolioView(portfolioQueryController, gameController));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(transactionQueryController));

    return pages;
  }

  private void navigateToStockDetail(Stock stock) {
    StockDetailController controller = new StockDetailController(
        gameId, buyShare, sellShare, portfolioQueryController, getPortfolio);
    StockDetailView view = new StockDetailView(
        stock, controller, () -> navManager.navigateTo(PageId.MARKET));
    navManager.show(view);
  }
}
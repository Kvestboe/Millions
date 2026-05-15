package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.view.WindowConfig;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private NavigationManager navManager;

  public GameViewCoordinator(GameSessionControllerBundle bundle) {
    this.bundle = bundle;
  }

  public Scene getScene() {
    navManager = new NavigationManager(buildPages());
    NavBar navBar = new NavBar(navManager::navigateTo);

    BorderPane root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());

    navManager.navigateTo(PageId.DASHBOARD);

    bundle.market().setOnStockSelected(this::navigateToStockDetail);

    Scene scene = new Scene(root, WindowConfig.WIDTH, WindowConfig.HEIGHT);
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
        stock,
        bundle.stockQuery(),
        bundle.orderPlacement(),
        () -> navManager.navigateTo(PageId.MARKET));
    navManager.show(view);
  }
}

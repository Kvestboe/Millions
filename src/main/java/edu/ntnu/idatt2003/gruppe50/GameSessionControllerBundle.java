package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItemFactory;
import edu.ntnu.idatt2003.gruppe50.ui.controller.DashboardQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrderPlacementController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.ShopController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;

/**
 * Holds all controllers wired up for a single active game session.
 *
 * <p>Acts as a per-session composition root: when a game is loaded or started,
 * one bundle is built by combining the application-wide {@link AppModule}
 * (use cases, repository, mappers) with the loaded {@link GameSession}. The
 * UI layer reaches its controllers through this bundle.
 *
 * @param session          the loaded game session this bundle belongs to
 * @param game             controller for game-level actions (buy, sell, advance week)
 * @param dashboard        controller exposing dashboard data
 * @param market           controller exposing market data
 * @param portfolio        controller exposing portfolio data
 * @param transactions     controller exposing transaction history and trading log
 * @param stockQuery       controller for detailed stock views
 * @param orderPlacement   controller for placing buy/sell and limit orders
 * @param ordersController controller for viewing and cancelling pending orders
 * @param shop             controller for shop and coin-exchange actions
 */
public record GameSessionControllerBundle(
    GameSession session,
    GameController game,
    DashboardQueryController dashboard,
    MarketQueryController market,
    PortfolioQueryController portfolio,
    TransactionQueryController transactions,
    StockDetailQueryController stockQuery,
    OrderPlacementController orderPlacement,
    OrdersController ordersController,
    ShopController shop
) {
  /**
   * Builds a session-scoped bundle by wiring use cases from {@code m} to the
   * given session.
   *
   * @param m       application-wide module providing use cases and dependencies
   * @param session the game session this bundle should serve
   */
  public GameSessionControllerBundle(AppModule m, GameSession session) {
    this(
        session,
        new GameController(session.getGameId(), m.buyShare, m.sellShare, m.advanceWeek),
        new DashboardQueryController(
            session.getGameId(),
            m.getGoalProgress,
            m.getStatusProgress,
            m.getWeeklyMovers,
            session.getExchange(),
            m.getNotifications),
        new MarketQueryController(session.getGameId(), m.getMarket, session.getExchange()),
        new PortfolioQueryController(session.getGameId(), m.getPortfolio, session.getExchange()),
        new TransactionQueryController(session.getGameId(), m.getTransactions, m.getTradingLog,
            session.getExchange()),
        new StockDetailQueryController(
            session.getGameId(), m.getPortfolio, m.getStockDetail, m.getTransactionMarkers,
            m.previewOrder, session.getExchange()),
        new OrderPlacementController(
            session.getGameId(),
            m.buyShare,
            m.sellShare,
            m.buyLimitOrder,
            m.sellLimitOrder,
            m.stopLossOrder
        ),
        new OrdersController(session.getGameId(), m.getPendingOrders, m.cancelOrder,
            session.getExchange()),
        new ShopController(
            session.getGameId(),
            new Shop(
                session.getCoinExchange(),
                ShopItemFactory.createDefaultItems()
            ),
            session.getPlayer(),
            session.getDifficulty(),
            m.buyCoins,
            m.purchaseShopItem
        )
    );
  }
}
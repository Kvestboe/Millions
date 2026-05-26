package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.shop.CoinExchange;
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
  public GameSessionControllerBundle(AppModule m, GameSession session) {
    this(
        session,
        new GameController(session.getGameId(), m.buyShare, m.sellShare, m.advanceWeek),
        new DashboardQueryController(session.getGameId(), m.getGoalProgress, session.getExchange(), m.getStatusProgress),
        new MarketQueryController(session.getGameId(), m.getMarket, session.getExchange()),
        new PortfolioQueryController(session.getGameId(), m.getPortfolio, session.getExchange()),
        new TransactionQueryController(session.getGameId(), m.getTransactions, m.getTradingLog, session.getExchange()),
        new StockDetailQueryController(
            session.getGameId(), m.getPortfolio, m.getStockDetail, m.getTransactionMarkers, m.previewOrder, session.getExchange()),
        new OrderPlacementController(
            session.getGameId(),
            m.buyShare,
            m.sellShare,
            m.buyLimitOrder,
            m.sellLimitOrder,
            m.stopLossOrder
        ),
        new OrdersController(session.getGameId(), m.getPendingOrders, session.getExchange()),
        new ShopController(
            session.getGameId(),
            new Shop(
                new CoinExchange(session.getPlayer().getStartingMoney()),
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
package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import edu.ntnu.idatt2003.gruppe50.domain.shop.CoinExchange;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItemFactory;
import edu.ntnu.idatt2003.gruppe50.ui.controller.ShopController;

public record GameSessionControllerBundle(
    GameSession session,
    GameController game,
    MarketQueryController market,
    PortfolioQueryController portfolio,
    TransactionQueryController transactions,
    StockDetailController stockDetail,
    OrdersController ordersController,
    ShopController shop
) {
  public GameSessionControllerBundle(AppModule m, GameSession session) {
    this(
        session,
        new GameController(session.getGameId(), m.buyShare, m.sellShare, m.advanceWeek),
        new MarketQueryController(session.getGameId(),m.getMarket, session.getExchange()),
        new PortfolioQueryController(session.getGameId(), m.getPortfolio, session.getExchange()),
        new TransactionQueryController(session.getGameId(), m.getTransactions, session.getExchange()),
        new StockDetailController(
            session.getGameId(),
            m.buyShare,
            m.sellShare,
            m.getPortfolio,
            m.buyLimitOrder,
            m.sellLimitOrder,
            m.stopLossOrder,
            m.previewOrder
        ),
        new OrdersController(session.getGameId(), m.getPendingOrders, session.getExchange()),
        new ShopController(
            new Shop(
                new CoinExchange(session.getPlayer().getStartingMoney()),
                ShopItemFactory.createDefaultItems()
            ),
            session.getPlayer(),
            session.getDifficulty()
        )
    );
  }
}

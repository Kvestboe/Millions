package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.OrdersController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.StockDetailController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;

public record GameSessionControllerBundle(
    GameController game,
    MarketQueryController market,
    PortfolioQueryController portfolio,
    TransactionQueryController transactions,
    StockDetailController stockDetail,
    OrdersController ordersController
) {
  public GameSessionControllerBundle(AppModule m, GameSession session) {
    this(
        new GameController(session.getGameId(), m.buyShare, m.sellShare, m.advanceWeek),
        new MarketQueryController(session.getGameId(),m.getMarket, session.getExchange(), null),
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
        new OrdersController(session.getGameId(), m.getPendingOrders)
    );
  }
}

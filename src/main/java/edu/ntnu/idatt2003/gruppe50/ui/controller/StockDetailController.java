package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView;

import java.util.Optional;
import java.util.UUID;

public class StockDetailController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final PortfolioQueryController portfolioQueryController;
  private final GetPortfolioUseCase getPortfolio;
  private final PlaceBuyLimitOrderUseCase placeBuyLimitOrder;
  private final PlaceSellLimitOrderUseCase placeSellLimitOrder;

  public StockDetailController(UUID gameId, BuyShareUseCase buyShare, SellShareUseCase sellShare, PortfolioQueryController portfolioQueryController, GetPortfolioUseCase getPortfolio, PlaceBuyLimitOrderUseCase placeBuyLimitOrderUseCase, PlaceSellLimitOrderUseCase sellLimitOrder) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
    this.portfolioQueryController = portfolioQueryController;
    this.getPortfolio = getPortfolio;
    this.placeBuyLimitOrder = placeBuyLimitOrderUseCase;
    this.placeSellLimitOrder = sellLimitOrder;
  }

  public Optional<ShareData> getHolding(String symbol) {
    return portfolioQueryController.getPortfolio().shares().stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }

  public void placeOrder(DraftOrder draftOrder) {
    if (draftOrder.isLimit()) {
      placeLimitOrder(draftOrder);
    } else {
      placeMarketOrder(draftOrder);
    }
  }

  public void placeMarketOrder(DraftOrder draftOrder) {
    if (draftOrder.side() == OrderFormView.Side.BUY) {
      buyShare.execute(new BuyShareUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity()
      ));
    } else {
      sellShare.execute(new SellShareUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity()
      ));
    }
  }

  public void placeLimitOrder(DraftOrder draftOrder) {
    if (draftOrder.side() == OrderFormView.Side.BUY) {
      placeBuyLimitOrder.execute(new PlaceBuyLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
    } else {
      placeSellLimitOrder.execute(new PlaceSellLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
    }
  }
}

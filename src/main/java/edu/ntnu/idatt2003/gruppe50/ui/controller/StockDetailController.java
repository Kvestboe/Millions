package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;

import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceStopLossOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.application.query.ShareDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StockDetailController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final GetPortfolioUseCase getPortfolio;
  private final PlaceBuyLimitOrderUseCase placeBuyLimitOrder;
  private final PlaceSellLimitOrderUseCase placeSellLimitOrder;
  private final PlaceStopLossOrderUseCase placeStopLossOrder;

//  public StockDetailController(UUID gameId, BuyShareUseCase buyShare, SellShareUseCase sellShare, PortfolioQueryController portfolioQueryController, GetPortfolioUseCase getPortfolio) {
  public StockDetailController(UUID gameId, BuyShareUseCase buyShare, SellShareUseCase sellShare, GetPortfolioUseCase getPortfolio, PlaceBuyLimitOrderUseCase placeBuyLimitOrderUseCase, PlaceSellLimitOrderUseCase sellLimitOrder, PlaceStopLossOrderUseCase placeStopLossOrder) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
//    this.portfolioQueryController = portfolioQueryController;
    this.getPortfolio = getPortfolio;
    this.placeBuyLimitOrder = placeBuyLimitOrderUseCase;
    this.placeSellLimitOrder = sellLimitOrder;
    this.placeStopLossOrder = placeStopLossOrder;
  }
  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

  public Optional<ShareDto> getHolding(String symbol) {
    List<ShareDto> portfolio = getPortfolio.execute(new Request(gameId)).shares();
    return portfolio.stream()
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
    if (draftOrder.side() == OrderFormView.Side.BUY
        && draftOrder.orderType() == OrderFormView.OrderType.TARGET_PRICE) {

      placeBuyLimitOrder.execute(new PlaceBuyLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    if (draftOrder.side() == OrderFormView.Side.SELL
        && draftOrder.orderType() == OrderFormView.OrderType.TARGET_PRICE) {

      placeSellLimitOrder.execute(new PlaceSellLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    if (draftOrder.side() == OrderFormView.Side.SELL
        && draftOrder.orderType() == OrderFormView.OrderType.STOP_LOSS) {

      placeStopLossOrder.execute(new PlaceStopLossOrderUseCase.Request(
          gameId,
          draftOrder.stock().getSymbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    throw new IllegalArgumentException("Unsupported order type");
  }
}

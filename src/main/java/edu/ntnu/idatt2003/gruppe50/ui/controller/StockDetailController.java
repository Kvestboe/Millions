package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;

import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceStopLossOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.application.query.OrderType;

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
  private final PreviewOrderUseCase previewOrderUseCase;

  public StockDetailController(
      UUID gameId,
      BuyShareUseCase buyShare,
      SellShareUseCase sellShare,
      GetPortfolioUseCase getPortfolio,
      PlaceBuyLimitOrderUseCase placeBuyLimitOrderUseCase,
      PlaceSellLimitOrderUseCase sellLimitOrder,
      PlaceStopLossOrderUseCase placeStopLossOrder,
      PreviewOrderUseCase previewOrderUseCase
  ) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
    this.getPortfolio = getPortfolio;
    this.placeBuyLimitOrder = placeBuyLimitOrderUseCase;
    this.placeSellLimitOrder = sellLimitOrder;
    this.placeStopLossOrder = placeStopLossOrder;
    this.previewOrderUseCase = previewOrderUseCase;
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
    if (draftOrder.side() == OrderSide.BUY) {
      buyShare.execute(new BuyShareUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity()
      ));
    } else {
      sellShare.execute(new SellShareUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity()
      ));
    }
  }

  public void placeLimitOrder(DraftOrder draftOrder) {
    if (draftOrder.side() == OrderSide.BUY
        && draftOrder.orderType() == OrderType.TARGET_PRICE) {

      placeBuyLimitOrder.execute(new PlaceBuyLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    if (draftOrder.side() == OrderSide.SELL
        && draftOrder.orderType() == OrderType.TARGET_PRICE) {

      placeSellLimitOrder.execute(new PlaceSellLimitOrderUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    if (draftOrder.side() == OrderSide.SELL
        && draftOrder.orderType() == OrderType.STOP_LOSS) {

      placeStopLossOrder.execute(new PlaceStopLossOrderUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity(),
          draftOrder.targetPrice(),
          draftOrder.duration()
      ));
      return;
    }

    throw new IllegalArgumentException("Unsupported order type");
  }

  public UUID gameId() {
    return gameId;
  }

  public PreviewOrderUseCase previewOrderUseCase() {
    return previewOrderUseCase;
  }
}

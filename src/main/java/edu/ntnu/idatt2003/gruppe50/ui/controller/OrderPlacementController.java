package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceStopLossOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import java.util.UUID;

public class OrderPlacementController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final PlaceBuyLimitOrderUseCase placeBuyLimitOrder;
  private final PlaceSellLimitOrderUseCase placeSellLimitOrder;
  private final PlaceStopLossOrderUseCase placeStopLossOrder;

  public OrderPlacementController(
      UUID gameId,
      BuyShareUseCase buyShare,
      SellShareUseCase sellShare,
      PlaceBuyLimitOrderUseCase placeBuyLimitOrder,
      PlaceSellLimitOrderUseCase placeSellLimitOrder,
      PlaceStopLossOrderUseCase placeStopLossOrder
  ) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
    this.placeBuyLimitOrder = placeBuyLimitOrder;
    this.placeSellLimitOrder = placeSellLimitOrder;
    this.placeStopLossOrder = placeStopLossOrder;
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
}

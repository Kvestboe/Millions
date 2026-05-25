package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceStopLossOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderReceipt;

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

  public OrderReceipt placeOrder(DraftOrder draftOrder) {
    if (draftOrder.isLimit()) {
      return placeLimitOrder(draftOrder);
    } else {
      return placeMarketOrder(draftOrder);
    }
  }


  private OrderReceipt placeMarketOrder(DraftOrder draftOrder) {
    if (draftOrder.side() == OrderSide.BUY) {
      BuyShareUseCase.Response r = buyShare.execute(new BuyShareUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity()
      ));
      return new OrderReceipt.ExecutedReceipt(
          OrderSide.BUY,
          r.symbol(),
          r.quantity(),
          r.totalAmount(),
          r.newHoldingQuantity(),
          r.week()
      );
    } else {
      SellShareUseCase.Response r = sellShare.execute(new SellShareUseCase.Request(
          gameId,
          draftOrder.stock().symbol(),
          draftOrder.quantity()
      ));
      return new OrderReceipt.ExecutedReceipt(
          OrderSide.SELL,
          r.symbol(),
          r.quantity(),
          r.totalAmount(),
          r.newHoldingQuantity(),
          r.week()
      );
    }
  }

  private OrderReceipt placeLimitOrder(DraftOrder draftOrder) {
    if (draftOrder.side() == OrderSide.BUY
        && draftOrder.orderType() == OrderType.TARGET_PRICE) {
      PlaceBuyLimitOrderUseCase.Response r = placeBuyLimitOrder.execute(
          new PlaceBuyLimitOrderUseCase.Request(
              gameId,
              draftOrder.stock().symbol(),
              draftOrder.quantity(),
              draftOrder.targetPrice(),
              draftOrder.duration()
          ));
      return new OrderReceipt.PendingReceipt(
          OrderSide.BUY,
          OrderType.TARGET_PRICE,
          r.symbol(),
          r.quantity(),
          r.targetPrice(),
          r.placedAtWeek(),
          r.expiresAtWeek()
      );
    }

    if (draftOrder.side() == OrderSide.SELL
        && draftOrder.orderType() == OrderType.TARGET_PRICE) {
      PlaceSellLimitOrderUseCase.Response r = placeSellLimitOrder.execute(
          new PlaceSellLimitOrderUseCase.Request(
              gameId,
              draftOrder.stock().symbol(),
              draftOrder.quantity(),
              draftOrder.targetPrice(),
              draftOrder.duration()
          ));
      return new OrderReceipt.PendingReceipt(
          OrderSide.SELL,
          OrderType.TARGET_PRICE,
          r.symbol(),
          r.quantity(),
          r.targetPrice(),
          r.placedAtWeek(),
          r.expiresAtWeek()
      );
    }

    if (draftOrder.side() == OrderSide.SELL
        && draftOrder.orderType() == OrderType.STOP_LOSS) {
      PlaceStopLossOrderUseCase.Response r = placeStopLossOrder.execute(
          new PlaceStopLossOrderUseCase.Request(
              gameId,
              draftOrder.stock().symbol(),
              draftOrder.quantity(),
              draftOrder.targetPrice(),
              draftOrder.duration()
          ));
      return new OrderReceipt.PendingReceipt(
          OrderSide.SELL,
          OrderType.STOP_LOSS,
          r.symbol(),
          r.quantity(),
          r.targetPrice(),
          r.placedAtWeek(),
          r.expiresAtWeek()
      );
    }

    throw new IllegalArgumentException("Unsupported order type");
  }
}
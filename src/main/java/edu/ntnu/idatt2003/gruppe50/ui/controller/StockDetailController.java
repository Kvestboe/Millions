package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;

import java.math.BigDecimal;
import java.util.UUID;

public class StockDetailController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;

  public StockDetailController(UUID gameId, BuyShareUseCase buyShare) {
    this.gameId = gameId;
    this.buyShare = buyShare;
  }

  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

}

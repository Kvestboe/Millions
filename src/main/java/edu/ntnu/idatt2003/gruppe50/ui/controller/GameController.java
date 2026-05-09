package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.application.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.SellShareUseCase;
import java.math.BigDecimal;
import java.util.UUID;

public final class GameController {
  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final AdvanceWeekUseCase advanceWeek;

  public GameController(
      UUID gameId,
      BuyShareUseCase buyShare,
      SellShareUseCase sellShare,
      AdvanceWeekUseCase advanceWeek
  ) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
    this.advanceWeek = advanceWeek;
  }

  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

  public void sell(UUID shareId) {
    sellShare.execute(new SellShareUseCase.Request(gameId, shareId));
  }

  public void advanceWeek() {
    advanceWeek.execute(new AdvanceWeekUseCase.Request(gameId));
  }
}

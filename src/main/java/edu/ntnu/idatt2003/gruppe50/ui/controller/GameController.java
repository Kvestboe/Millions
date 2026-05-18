package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;

import java.math.BigDecimal;
import java.util.UUID;

public final class GameController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final AdvanceWeekUseCase advanceWeek;
  private GameOutcomeListener outcomeListener;

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

  public void sell(String symbol, BigDecimal quantity) {
    sellShare.execute(new SellShareUseCase.Request(gameId, symbol, quantity));
  }

  public void advanceWeek() {
    AdvanceWeekUseCase.Result result = advanceWeek.execute(new AdvanceWeekUseCase.Request(gameId));
    if (result.outcome() != GameOutcome.ONGOING && outcomeListener != null) {
      outcomeListener.onOutcome(result.outcome());
    }
  }

  public void setOutcomeListener(GameOutcomeListener listener) {
    this.outcomeListener = listener;
  }

}

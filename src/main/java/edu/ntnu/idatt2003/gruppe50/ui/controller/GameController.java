package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;
import java.math.BigDecimal;
import java.util.UUID;

/** Coordinates basic game actions from the UI layer. */
public final class GameController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final AdvanceWeekUseCase advanceWeek;
  private GameOutcomeListener outcomeListener;

  /**
   * Creates a controller for a specific game session.
   *
   * @param gameId id of the game session
   * @param buyShare use case for buying shares
   * @param sellShare use case for selling shares
   * @param advanceWeek use case for advancing the game week
   */
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

  /**
   * Buys shares in the current game session.
   *
   * @param symbol stock symbol to buy
   * @param quantity quantity to buy
   */
  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

  /**
   * Sells shares in the current game session.
   *
   * @param symbol stock symbol to sell
   * @param quantity quantity to sell
   */
  public void sell(String symbol, BigDecimal quantity) {
    sellShare.execute(new SellShareUseCase.Request(gameId, symbol, quantity));
  }

  /**
   * Advances the current game session by one week.
   *
   * <p>If the game reaches a non-ongoing outcome, the registered outcome listener
   * is notified.
   */
  public void advanceWeek() {
    AdvanceWeekUseCase.Result result = advanceWeek.execute(new AdvanceWeekUseCase.Request(gameId));
    if (result.outcome() != GameOutcome.ONGOING && outcomeListener != null) {
      outcomeListener.onOutcome(result.outcome());
    }
  }

  /**
   * Registers a listener for game outcome changes.
   *
   * @param listener listener to notify when the game reaches a non-ongoing outcome
   */
  public void setOutcomeListener(GameOutcomeListener listener) {
    this.outcomeListener = listener;
  }

}

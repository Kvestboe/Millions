package edu.ntnu.idatt2003.gruppe50.domain.game;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class GameSession {
  private final UUID gameId;
  private final Player player;
  private final Exchange exchange;
  private GameSessionState state;
  private final LocalDate runStartedAt;
  private LocalDate lastPlayed;

  private GameSession(
      UUID gameId,
      Player player,
      Exchange exchange,
      GameSessionState state,
      LocalDate runStartedAt,
      LocalDate lastPlayed
  ) {
    this.gameId = gameId;
    this.player = player;
    this.exchange = exchange;
    this.state = state;
    this.runStartedAt = runStartedAt;
    this.lastPlayed = lastPlayed;
  }

  public static GameSession createNew(Player player, Exchange exchange) {
    Validate.notNull(player, "Player");
    Validate.notNull(exchange, "Exchange");

    return new GameSession(
        UUID.randomUUID(),
        player,
        exchange,
        GameSessionState.ACTIVE,
        LocalDate.now(),
        LocalDate.now()
    );
  }

  public static GameSession rehydrate(
      UUID gameId,
      Player player,
      Exchange exchange,
      GameSessionState state,
      LocalDate runStartedAt,
      LocalDate lastPlayed
  ) {
    Validate.notNull(gameId, "Game id");
    Validate.notNull(player, "Player");
    Validate.notNull(exchange, "Exchange");
    Validate.notNull(state, "Game state");
    Validate.notNull(runStartedAt, "Run started at date");
    Validate.notNull(lastPlayed, "Last played date");
    return new GameSession(gameId, player, exchange, state, runStartedAt, lastPlayed);
  }

  public void markOpened() {
    lastPlayed = LocalDate.now();
  }

  public void buy(String symbol, BigDecimal quantity) {
    ensureActive();
    exchange.buy(symbol, quantity, player);
  }

  public void sell(UUID shareId) {
    ensureActive();
    exchange.sell(shareId, player);
  }

  public void advanceWeek() {
    ensureActive();
    exchange.advance();
  }

  public void finish() {
    state = GameSessionState.FINISHED;
  }

  public GameSessionState getState() {
    return state;
  }

  public UUID getGameId() {
    return gameId;
  }

  public Player getPlayer() {
    return player;
  }

  public Exchange getExchange() {
    return exchange;
  }

  public LocalDate getRunStartedAt() {
    return runStartedAt;
  }

  public LocalDate getLastPlayed() {
    return lastPlayed;
  }

  private void ensureActive() {
    if (state == GameSessionState.FINISHED) {
      throw new GameSessionFinishedException();
    }
  }
}

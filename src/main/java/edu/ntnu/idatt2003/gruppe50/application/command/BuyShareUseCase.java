package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.UUID;

/** Buys shares inside a game session and saves the updated state. */
public final class BuyShareUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public BuyShareUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a buy operation for an existing game session.
   *
   * @param request input with game id, symbol and quantity
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public void execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    session.buy(request.symbol(), request.quantity());
    repository.save(session);
  }

  /**
   * Input for buying shares in a session.
   *
   * @param gameId id of the game session
   * @param symbol stock symbol to buy
   * @param quantity quantity to buy
   */
  public record Request(UUID gameId, String symbol, BigDecimal quantity) {}
}

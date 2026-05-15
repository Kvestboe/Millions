package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/** Starts a new game session from player and exchange input. */
public final class StartGameSessionUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to save game sessions
   */
  public StartGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates and saves a new game session.
   *
   * @param request input containing player and exchange state
   * @return response containing newly created game id
   */
  public Response execute(Request request) {
    GameSession session = GameSession.createNew(request.player(), request.exchange(), request.difficulty);
    repository.save(session);
    return new Response(session.getGameId());
  }

  /**
   * Input for starting a new game session.
   *
   * @param player player state to start with
   * @param exchange exchange state to start with
   */
  public record Request(Player player, Exchange exchange, Difficulty difficulty) {}

  /**
   * Output from starting a new game session.
   *
   * @param gameId newly created game-session id
   */
  public record Response(UUID gameId) {}
}

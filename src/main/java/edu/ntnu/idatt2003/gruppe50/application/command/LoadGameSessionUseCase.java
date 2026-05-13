package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/** Loads a game session, marks it as opened, and returns summary state. */
public final class LoadGameSessionUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save sessions
   */
  public LoadGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Loads, updates and returns session data.
   *
   * @param request input containing game id
   * @return response with game id and current state
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    session.markOpened();
    repository.save(session);

    return new Response(session.getGameId(), session.getState());
  }

  /**
   * Input for loading a game session.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}

  /**
   * Output from loading a game session.
   *
   * @param gameId id of the loaded game session
   * @param state current state of the session
   */
  public record Response(UUID gameId, GameSessionState state) {}
}

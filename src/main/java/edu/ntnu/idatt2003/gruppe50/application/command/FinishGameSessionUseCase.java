package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/**
 * Finishes an existing game session and saves state.
 */
public final class FinishGameSessionUseCase {
  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save sessions
   */
  public FinishGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Marks a session as finished and saves it.
   *
   * @param request input containing game id
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public void execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    session.finish();
    repository.save(session);
  }

  /**
   * Input for finishing a game session.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}
}

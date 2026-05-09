package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/**
 * Advances game session by one week and saves the change.
 */
public final class AdvanceWeekUseCase {
  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public AdvanceWeekUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes the use case for a given game id.
   *
   * @param request input request containing game id
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public void execute(Request request) {
    GameSession session = repository.findById(request.gameId)
        .orElseThrow(GameSessionNotFoundException::new);

    session.advanceWeek();
    repository.save(session);
  }

  /**
   * Input for advancing a session week.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}
}

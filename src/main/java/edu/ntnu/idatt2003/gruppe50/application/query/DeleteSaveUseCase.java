package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/** Deletes a saved game session. */
public final class DeleteSaveUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to delete game sessions
   */
  public DeleteSaveUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Deletes the saved session with the given id.
   *
   * @param gameId id of the game session to delete
   */
  public void execute(UUID gameId) {
    repository.delete(gameId);
  }
}

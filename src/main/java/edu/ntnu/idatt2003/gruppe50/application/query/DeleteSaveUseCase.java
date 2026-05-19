package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

public final class DeleteSaveUseCase {

  private final GameSessionRepository repository;

  public DeleteSaveUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID gameId) {
    repository.delete(gameId);
  }
}

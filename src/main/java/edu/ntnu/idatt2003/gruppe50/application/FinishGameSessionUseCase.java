package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

public final class FinishGameSessionUseCase {
  private final GameSessionRepository repository;

  public FinishGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public void execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    session.finish();
    repository.save(session);
  }

  public record Request(UUID gameId) {}
}

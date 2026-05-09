package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

public final class LoadGameSessionUseCase {
  private final GameSessionRepository repository;

  public LoadGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    session.markOpened();
    repository.save(session);

    return new Response(session.getGameId(), session.getState());
  }

  public record Request(UUID gameId) {}

  public record Response(UUID gameId, GameSessionState state) {}
}

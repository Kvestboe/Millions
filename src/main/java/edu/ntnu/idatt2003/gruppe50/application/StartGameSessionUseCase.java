package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

public final class StartGameSessionUseCase {
  private final GameSessionRepository repository;

  public StartGameSessionUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session = GameSession.createNew(request.player(), request.exchange());
    repository.save(session);
    return new Response(session.getGameId());
  }

  public record Request(Player player, Exchange exchange) {}

  public record Response(UUID gameId) {}
}

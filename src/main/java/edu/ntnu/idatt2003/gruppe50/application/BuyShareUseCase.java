package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.UUID;

public final class BuyShareUseCase {
  private final GameSessionRepository repository;

  public BuyShareUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public void execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    session.buy(request.symbol(), request.quantity());
    repository.save(session);
  }

  public record Request(UUID gameId, String symbol, BigDecimal quantity) {}

}

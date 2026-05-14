package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.UUID;

/** Places a stop-loss order inside a game session and saves the updated state. */
public final class PlaceStopLossOrderUseCase {

  private final GameSessionRepository repository;

  public PlaceStopLossOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public void execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    session.placeStopLossOrder(
        request.symbol(),
        request.quantity(),
        request.targetPrice(),
        request.duration()
    );

    repository.save(session);
  }

  public record Request(
      UUID gameId,
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {}
}
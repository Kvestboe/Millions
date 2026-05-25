package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.StopLossOrder;

import java.math.BigDecimal;
import java.util.UUID;

/** Places a stop-loss order inside a game session and saves the updated state. */
public final class PlaceStopLossOrderUseCase {

  private final GameSessionRepository repository;

  public PlaceStopLossOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    StopLossOrder order = session.placeStopLossOrder(
        request.symbol(), request.quantity(),
        request.targetPrice(), request.duration()
    );
    repository.save(session);

    return new Response(
        request.symbol(),
        request.quantity(),
        request.targetPrice(),
        order.getCreatedWeek(),
        order.getExpiryWeek()
    );
  }

  public record Request(
      UUID gameId,
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {}

  public record Response(
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int placedAtWeek,
      int expiresAtWeek
  ) {}
}
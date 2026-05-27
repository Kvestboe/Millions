package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import java.util.UUID;

/**
 * Use case for cancelling a pending limit order.
 */
public final class CancelOrderUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates a new cancel order use case.
   *
   * @param repository the repository used to load and save game sessions
   */
  public CancelOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Cancels a pending order from a game session.
   *
   * @param request the cancel order request
   * @return true if an order was cancelled, false otherwise
   * @throws GameSessionNotFoundException if the game session does not exist
   */
  public boolean execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    LimitOrder order = session.getPendingOrders().stream()
        .filter(o -> matches(o, request.order()))
        .findFirst()
        .orElse(null);

    if (order == null) {
      return false;
    }

    boolean cancelled = session.getExchange().cancelOrder(order);

    if (cancelled) {
      repository.save(session);
    }

    return cancelled;
  }

  private boolean matches(LimitOrder order, PendingOrderDto dto) {
    return order.label().equals(dto.type())
        && order.getStock().getSymbol().equals(dto.symbol())
        && order.getQuantity().compareTo(dto.quantity()) == 0
        && order.getTargetPrice().compareTo(dto.targetPrice()) == 0
        && order.getCreatedWeek() == dto.createdWeek()
        && order.getExpiryWeek() == dto.expiryWeek();
  }

  /**
   * Request for cancelling a pending order.
   *
   * @param gameId the id of the game session
   * @param order  the order to cancel
   */
  public record Request(UUID gameId, PendingOrderDto order) {
  }
}
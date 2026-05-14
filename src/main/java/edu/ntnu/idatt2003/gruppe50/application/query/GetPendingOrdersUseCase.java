package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import java.util.List;
import java.util.UUID;

public final class GetPendingOrdersUseCase {

  private final GameSessionRepository repository;

  public GetPendingOrdersUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public List<LimitOrder> execute(UUID gameId) {
    return repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new)
        .getPendingOrders();
  }
}
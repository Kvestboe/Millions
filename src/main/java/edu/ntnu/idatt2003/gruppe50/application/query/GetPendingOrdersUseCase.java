package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;
import java.util.UUID;

public final class GetPendingOrdersUseCase {

  private final GameSessionRepository repository;

  public GetPendingOrdersUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public List<PendingOrderDto> execute(UUID gameId) {
    return repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new)
        .getPendingOrders()
        .stream()
        .map(order -> new PendingOrderDto(
            order.label(),
            order.getStock().getSymbol(),
            order.getStock().getCompany(),
            order.getQuantity(),
            order.getTargetPrice(),
            order.getCreatedWeek(),
            order.getExpiryWeek()
        ))
        .toList();
  }
}
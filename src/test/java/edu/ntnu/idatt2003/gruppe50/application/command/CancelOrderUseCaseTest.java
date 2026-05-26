package edu.ntnu.idatt2003.gruppe50.application.command;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PendingOrderDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CancelOrderUseCaseTest {

  private GameSessionRepository repository;
  private CancelOrderUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new CancelOrderUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    PendingOrderDto dummy = new PendingOrderDto(
        "BUY_LIMIT", "AAPL", "Apple", bd(1), bd(100), 1, 4);

    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new CancelOrderUseCase.Request(UUID.randomUUID(), dummy)));
  }

  @Test
  void execute_matchingOrder_removesItFromPending() {
    session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 3);
    PendingOrderDto dto = toDto(session.getPendingOrders().getFirst());

    boolean result = useCase.execute(new CancelOrderUseCase.Request(session.getGameId(), dto));

    assertTrue(result);
    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertTrue(saved.getPendingOrders().isEmpty());
  }

  @Test
  void execute_noMatchingOrder_returnsFalse() {
    PendingOrderDto unknown = new PendingOrderDto(
        "BUY_LIMIT", "AAPL", "Apple", bd(1), bd(100), 1, 4);

    boolean result = useCase.execute(new CancelOrderUseCase.Request(session.getGameId(), unknown));

    assertFalse(result);
  }

  @Test
  void execute_matchingOrder_persistsChange() {
    session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 3);
    PendingOrderDto dto = toDto(session.getPendingOrders().getFirst());

    useCase.execute(new CancelOrderUseCase.Request(session.getGameId(), dto));

    // Reload from repository to verify it was persisted
    GameSession reloaded = repository.findById(session.getGameId()).orElseThrow();
    assertTrue(reloaded.getPendingOrders().isEmpty());
  }

  @Test
  void execute_onlyCancelsMatchingOrder_otherOrdersUntouched() {
    session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 3);
    session.placeBuyLimitOrder("KOG", bd(2), bd(300), 5);

    LimitOrder aaplOrder = session.getPendingOrders().stream()
        .filter(o -> o.getStock().getSymbol().equals("AAPL"))
        .findFirst().orElseThrow();
    PendingOrderDto aaplDto = toDto(aaplOrder);

    useCase.execute(new CancelOrderUseCase.Request(session.getGameId(), aaplDto));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(1, saved.getPendingOrders().size());
    assertEquals("KOG", saved.getPendingOrders().getFirst().getStock().getSymbol());
  }

  private static PendingOrderDto toDto(LimitOrder order) {
    return new PendingOrderDto(
        order.label(),
        order.getStock().getSymbol(),
        order.getStock().getCompany(),
        order.getQuantity(),
        order.getTargetPrice(),
        order.getCreatedWeek(),
        order.getExpiryWeek()
    );
  }
}
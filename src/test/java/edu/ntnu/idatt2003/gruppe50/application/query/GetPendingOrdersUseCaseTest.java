package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetPendingOrdersUseCaseTest {

  private GameSessionRepository repository;
  private GetPendingOrdersUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetPendingOrdersUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(UUID.randomUUID()));
  }

  @Test
  void execute_noPendingOrders_returnsEmptyList() {
    assertTrue(useCase.execute(session.getGameId()).isEmpty());
  }

  @Test
  void execute_withPendingOrder_returnsMappedDto() {
    session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 3);

    var orders = useCase.execute(session.getGameId());

    assertEquals(1, orders.size());
    assertEquals("AAPL", orders.getFirst().symbol());
  }
}

package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaceSellLimitOrderUseCaseTest {

  private GameSessionRepository repository;
  private PlaceSellLimitOrderUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new PlaceSellLimitOrderUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_addsOrderToPendingOrders() {
    useCase.execute(new PlaceSellLimitOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(1), bd(500), 3));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(1, saved.getPendingOrders().size());
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new PlaceSellLimitOrderUseCase.Request(
            UUID.randomUUID(), "AAPL", bd(1), bd(500), 3)));
  }

  @Test
  void execute_invalidSymbol_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> useCase.execute(new PlaceSellLimitOrderUseCase.Request(
            session.getGameId(), "", bd(1), bd(500), 3)));
  }
}

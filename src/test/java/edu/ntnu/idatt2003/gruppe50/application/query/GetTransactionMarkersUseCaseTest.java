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

public class GetTransactionMarkersUseCaseTest {

  private GameSessionRepository repository;
  private GetTransactionMarkersUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetTransactionMarkersUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new GetTransactionMarkersUseCase.Request(UUID.randomUUID(), "AAPL")));
  }

  @Test
  void execute_noTransactions_returnsEmptyWeekSets() {
    GetTransactionMarkersUseCase.Response response =
        useCase.execute(new GetTransactionMarkersUseCase.Request(session.getGameId(), "AAPL"));

    assertTrue(response.buyWeeks().isEmpty());
    assertTrue(response.sellWeeks().isEmpty());
  }

  @Test
  void execute_withBuyAndSell_returnsCorrectWeekSets() {
    session.buy("KOG", bd(1));
    session.buy("AAPL", bd(1));
    session.sell("AAPL", bd(1));

    GetTransactionMarkersUseCase.Response response =
        useCase.execute(new GetTransactionMarkersUseCase.Request(session.getGameId(), "AAPL"));

    assertFalse(response.buyWeeks().isEmpty());
    assertFalse(response.sellWeeks().isEmpty());
  }
}

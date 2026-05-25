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

public class GetTransactionsUseCaseTest {

  private GameSessionRepository repository;
  private GetTransactionsUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetTransactionsUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new GetTransactionsUseCase.Request(UUID.randomUUID())));
  }

  @Test
  void execute_noTransactions_returnsEmptyList() {
    GetTransactionsUseCase.Response response =
        useCase.execute(new GetTransactionsUseCase.Request(session.getGameId()));

    assertTrue(response.transactionDtoArchive().isEmpty());
  }

  @Test
  void execute_withTransactions_returnsMappedList() {
    session.buy("AAPL", bd(1));

    GetTransactionsUseCase.Response response =
        useCase.execute(new GetTransactionsUseCase.Request(session.getGameId()));

    assertEquals(1, response.transactionDtoArchive().size());
  }
}

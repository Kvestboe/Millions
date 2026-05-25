package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetMarketUseCaseTest {

  private GameSessionRepository repository;
  private GetMarketUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetMarketUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new GetMarketUseCase.Request(UUID.randomUUID(), null)));
  }

  @Test
  void execute_nullQuery_returnsAllStocks() {
    GetMarketUseCase.Response response =
        useCase.execute(new GetMarketUseCase.Request(session.getGameId(), null));

    assertEquals(2, response.stocks().size());
  }

  @Test
  void execute_blankQuery_returnsAllStocks() {
    GetMarketUseCase.Response response =
        useCase.execute(new GetMarketUseCase.Request(session.getGameId(), ""));

    assertEquals(2, response.stocks().size());
  }

  @Test
  void execute_matchingQuery_returnsFilteredStocks() {
    GetMarketUseCase.Response response =
        useCase.execute(new GetMarketUseCase.Request(session.getGameId(), "AAPL"));

    assertEquals(1, response.stocks().size());
    assertEquals("AAPL", response.stocks().getFirst().symbol());
  }
}

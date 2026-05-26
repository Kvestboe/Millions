package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetStockDetailUseCaseTest {

  private GameSessionRepository repository;
  private GetStockDetailUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetStockDetailUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new GetStockDetailUseCase.Request(UUID.randomUUID(), "AAPL")));
  }

  @Test
  void execute_knownSymbol_returnsStockDto() {
    Optional<StockDto> result =
        useCase.execute(new GetStockDetailUseCase.Request(session.getGameId(), "AAPL"));

    assertTrue(result.isPresent());
  }

  @Test
  void execute_unknownSymbol_returnsEmptyOptional() {
    Optional<StockDto> result =
        useCase.execute(new GetStockDetailUseCase.Request(session.getGameId(), "MSFT"));

    assertTrue(result.isEmpty());
  }
}

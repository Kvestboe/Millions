package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.TradingLogDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetTradingLogUseCaseTest {

  private GameSessionRepository repository;
  private GetTradingLogUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetTradingLogUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(UUID.randomUUID()));
  }

  @Test
  void execute_noTransactions_returnsZeroStats() {
    TradingLogDto dto = useCase.execute(session.getGameId());

    assertEquals(0, dto.totalTrades());
    assertEquals(0, dto.purchases());
    assertEquals(0, dto.sales());
    assertTrue(dto.recentActivity().isEmpty());
  }

  @Test
  void execute_withBuyAndSell_returnsCorrectCounts() {
    session.buy("AAPL", bd(1));
    session.sell("AAPL", bd(1));

    TradingLogDto dto = useCase.execute(session.getGameId());

    assertEquals(2, dto.totalTrades());
    assertEquals(1, dto.purchases());
    assertEquals(1, dto.sales());
  }
}

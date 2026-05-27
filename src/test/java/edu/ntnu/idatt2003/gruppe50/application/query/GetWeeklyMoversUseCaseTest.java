package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.WeeklyMoversDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetWeeklyMoversUseCaseTest {

  private GameSessionRepository repository;
  private GetWeeklyMoversUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetWeeklyMoversUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(UUID.randomUUID()));
  }

  @Test
  void execute_defaultLimit_returnsAtMostThreePerCategory() {
    WeeklyMoversDto dto = useCase.execute(session.getGameId());

    assertTrue(dto.topGainers().size() <= 3);
    assertTrue(dto.topLosers().size() <= 3);
  }

  @Test
  void execute_withExplicitLimit_respectsLimit() {
    WeeklyMoversDto dto = useCase.execute(session.getGameId(), 1);

    assertEquals(1, dto.topGainers().size());
    assertEquals(1, dto.topLosers().size());
  }

  @Test
  void execute_gainers_sortedByPercentChangeDescending() {
    Stock kog = session.getExchange().getStock("KOG");
    Stock aapl = session.getExchange().getStock("AAPL");

    kog.addNewSalesPrice(bd(363));     // 330 → 363 = +10%
    aapl.addNewSalesPrice(bd(500));    // 400 → 500 = +25%

    WeeklyMoversDto dto = useCase.execute(session.getGameId());

    assertEquals("AAPL", dto.topGainers().get(0).symbol());
    assertEquals("KOG", dto.topGainers().get(1).symbol());
  }

  @Test
  void execute_losers_sortedByPercentChangeAscending() {
    Stock kog = session.getExchange().getStock("KOG");
    Stock aapl = session.getExchange().getStock("AAPL");

    kog.addNewSalesPrice(bd(297));     // 330 → 297 = -10%
    aapl.addNewSalesPrice(bd(300));    // 400 → 300 = -25%

    WeeklyMoversDto dto = useCase.execute(session.getGameId());

    assertEquals("AAPL", dto.topLosers().get(0).symbol());
    assertEquals("KOG", dto.topLosers().get(1).symbol());
  }
}
package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.assertBigDecimalEquals;
import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetPortfolioUseCaseTest {

  private GameSessionRepository repository;
  private GetPortfolioUseCase getPortfolio;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    getPortfolio = new GetPortfolioUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_returnsPortfolio() {
    GetPortfolioUseCase.Response response =
        getPortfolio.execute(new GetPortfolioUseCase.Request(session.getGameId()));

    assertBigDecimalEquals(bd(10000), response.portfolio().cash());
    assertBigDecimalEquals(BigDecimal.ZERO, response.portfolio().portfolioValue());
    assertBigDecimalEquals(bd(10000), response.portfolio().netWorth());
    assertTrue(response.portfolio().shares().isEmpty());
  }

  @Test
  void execute_validRequest_withHoldings_returnsPortfolio() {
    session.buy("AAPL", bd(2));
    repository.save(session);

    GetPortfolioUseCase.Response response =
        getPortfolio.execute(new GetPortfolioUseCase.Request(session.getGameId()));

    assertBigDecimalEquals(bd(9196), response.portfolio().cash());
    assertBigDecimalEquals(bd(792), response.portfolio().portfolioValue());
    assertBigDecimalEquals(bd(9988), response.portfolio().netWorth());

    assertEquals(1, response.portfolio().shares().size());
    ShareDto share = response.portfolio().shares().getFirst();

    assertNotNull(share.shareId());
    assertEquals("AAPL", share.symbol());
    assertEquals("Apple", share.stock());
    assertBigDecimalEquals(bd(2), share.quantity());
    assertBigDecimalEquals(bd(400), share.purchasePrice());
    assertBigDecimalEquals(bd(400), share.currentPrice());
    assertBigDecimalEquals(bd(800), share.currentShareValue());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unknownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> getPortfolio.execute(new GetPortfolioUseCase.Request(unknownId)));
  }
}

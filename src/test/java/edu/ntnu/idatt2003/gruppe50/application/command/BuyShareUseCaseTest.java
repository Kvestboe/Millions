package edu.ntnu.idatt2003.gruppe50.application.command;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.assertBigDecimalEquals;
import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuyShareUseCaseTest {

  private GameSessionRepository repository;
  private BuyShareUseCase buyShare;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    buyShare = new BuyShareUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_updatesPortfolioAndMoney() {
    buyShare.execute(new BuyShareUseCase.Request(session.getGameId(), "AAPL", bd(2)));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(1, repositorySession.getPlayer().getPortfolio().getShares().size());
    assertBigDecimalEquals(bd(9196), repositorySession.getPlayer().getMoney());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unknownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> buyShare.execute(new BuyShareUseCase.Request(unknownId, "AAPL", bd(2))));
  }

  @Test
  void execute_nonExistentSymbol_throwsExceptionAndKeepsOriginal() {
    BigDecimal moneyBefore = session.getPlayer().getMoney();
    int sharesBefore = session.getPlayer().getPortfolio().getShares().size();

    assertThrows(
        NoSuchElementException.class,
        () -> buyShare.execute(new BuyShareUseCase.Request(session.getGameId(), "MSFT", bd(1))));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(moneyBefore, repositorySession.getPlayer().getMoney());
    assertEquals(sharesBefore, repositorySession.getPlayer().getPortfolio().getShares().size());
  }

  @Test
  void execute_invalidQuantity_throwsExceptionAndKeepOriginal() {
    BigDecimal moneyBefore = session.getPlayer().getMoney();
    int sharesBefore = session.getPlayer().getPortfolio().getShares().size();

    assertThrows(
        IllegalArgumentException.class,
        () -> buyShare.execute(new BuyShareUseCase.Request(session.getGameId(), "AAPL", bd(0))));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(moneyBefore, repositorySession.getPlayer().getMoney());
    assertEquals(sharesBefore, repositorySession.getPlayer().getPortfolio().getShares().size());
  }
}

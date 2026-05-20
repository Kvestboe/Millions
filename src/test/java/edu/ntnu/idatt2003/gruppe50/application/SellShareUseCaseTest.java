package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.assertBigDecimalEquals;
import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SellShareUseCaseTest {

  private GameSessionRepository repository;
  private SellShareUseCase sellShare;
  private BuyShareUseCase buyShare;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    sellShare = new SellShareUseCase(repository);
    buyShare = new BuyShareUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_sellsShare() {
    buyShare.execute(new BuyShareUseCase.Request(session.getGameId(), "AAPL", bd(2)));

    GameSession afterBuy = repository.findById(session.getGameId()).orElseThrow();
    Share share = afterBuy.getPlayer().getPortfolio().getShares().getFirst();

    sellShare.execute(new SellShareUseCase.Request(
        session.getGameId(),
        share.getStock().getSymbol(),
        share.getQuantity()
    ));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();

    assertEquals(0, repositorySession.getPlayer().getPortfolio().getShares().size());
    assertBigDecimalEquals(bd(9988), repositorySession.getPlayer().getMoney());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unknownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> sellShare.execute(new SellShareUseCase.Request(
            unknownId,
            "AAPL",
            bd(1)
        ))
    );
  }

  @Test
  void execute_unknownSymbol_throwsException() {
    BigDecimal moneyBefore = session.getPlayer().getMoney();
    int sharesBefore = session.getPlayer().getPortfolio().getShares().size();

    assertThrows(
        NoSuchElementException.class,
        () -> sellShare.execute(new SellShareUseCase.Request(
            session.getGameId(),
            "UNKNOWN",
            bd(1)
        ))
    );

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();

    assertBigDecimalEquals(moneyBefore, repositorySession.getPlayer().getMoney());
    assertEquals(sharesBefore, repositorySession.getPlayer().getPortfolio().getShares().size());
  }

  @Test
  void execute_notEnoughShares_throwsException() {
    buyShare.execute(new BuyShareUseCase.Request(session.getGameId(), "AAPL", bd(2)));

    GameSession afterBuy = repository.findById(session.getGameId()).orElseThrow();
    BigDecimal moneyBefore = afterBuy.getPlayer().getMoney();
    int sharesBefore = afterBuy.getPlayer().getPortfolio().getShares().size();

    assertThrows(
        IllegalStateException.class,
        () -> sellShare.execute(new SellShareUseCase.Request(
            session.getGameId(),
            "AAPL",
            bd(3)
        ))
    );

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();

    assertBigDecimalEquals(moneyBefore, repositorySession.getPlayer().getMoney());
    assertEquals(sharesBefore, repositorySession.getPlayer().getPortfolio().getShares().size());
  }
}
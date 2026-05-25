package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreviewOrderUseCaseTest {

  private GameSessionRepository repository;
  private PreviewOrderUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new PreviewOrderUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_buySide_returnsPositiveSubtotal() {
    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(2), OrderSide.BUY, null));

    assertTrue(response.subtotal().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void execute_buySide_totalGreaterThanSubtotal() {
    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(2), OrderSide.BUY, null));

    assertTrue(response.total().compareTo(response.subtotal()) > 0);
  }

  @Test
  void execute_buySide_withTargetPrice_useTargetInsteadOfMarketPrice() {
    BigDecimal targetPrice = bd(1);
    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(2), OrderSide.BUY, targetPrice));

    assertEquals(0, response.subtotal().compareTo(bd(2)));
  }

  @Test
  void execute_buySide_availableCashMatchesPlayerMoney() {
    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(1), OrderSide.BUY, null));

    assertEquals(0,
        response.availableCash().compareTo(session.getPlayer().getMoney()));
  }

  @Test
  void execute_sellSide_noShares_throwsIllegalStateException() {
    assertThrows(IllegalStateException.class,
        () -> useCase.execute(new PreviewOrderUseCase.Request(
            session.getGameId(), "AAPL", bd(1), OrderSide.SELL, null)));
  }

  @Test
  void execute_sellSide_partialQuantity_returnsResponse() {
    session.buy("AAPL", bd(3));

    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(2), OrderSide.SELL, null));

    assertNotNull(response);
    assertTrue(response.subtotal().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void execute_sellSide_moreThanOwned_throwsIllegalStateException() {
    session.buy("AAPL", bd(2));

    assertThrows(IllegalStateException.class,
        () -> useCase.execute(new PreviewOrderUseCase.Request(
            session.getGameId(), "AAPL", bd(10), OrderSide.SELL, null)));
  }

  @Test
  void execute_sellSide_acrossMultipleShares_computesFifoCostBasis() {
    session.buy("AAPL", bd(1));
    session.getExchange().getStock("AAPL").addNewSalesPrice(bd(600));
    session.buy("AAPL", bd(1));

    // Preview selling 2 — should use FIFO cost basis without throwing
    PreviewOrderUseCase.Response response = useCase.execute(new PreviewOrderUseCase.Request(
        session.getGameId(), "AAPL", bd(2), OrderSide.SELL, null));

    assertNotNull(response);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new PreviewOrderUseCase.Request(
            UUID.randomUUID(), "AAPL", bd(1), OrderSide.BUY, null)));
  }
}

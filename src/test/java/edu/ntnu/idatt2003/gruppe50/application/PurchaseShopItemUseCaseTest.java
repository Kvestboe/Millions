package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.command.PurchaseShopItemUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PurchaseShopItemUseCaseTest {

  private GameSessionRepository repository;
  private PurchaseShopItemUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new PurchaseShopItemUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validPurchase_playerOwnsTheme() throws InsufficientCoinsException {
    session.getPlayer().addCoins(15);

    useCase.execute(new PurchaseShopItemUseCase.Request(
        session.getGameId(), "theme-midnight-black"));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertTrue(saved.getPlayer().ownsTheme("midnight-black"));
  }

  @Test
  void execute_validPurchase_coinsAreDeducted() throws InsufficientCoinsException {
    session.getPlayer().addCoins(20);

    useCase.execute(new PurchaseShopItemUseCase.Request(
        session.getGameId(), "theme-midnight-black"));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(5, saved.getPlayer().getCoins());
  }

  @Test
  void execute_insufficientCoins_throwsInsufficientCoinsException() {
    assertThrows(InsufficientCoinsException.class,
        () -> useCase.execute(new PurchaseShopItemUseCase.Request(
            session.getGameId(), "theme-midnight-black")));
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new PurchaseShopItemUseCase.Request(
            UUID.randomUUID(), "theme-midnight-black")));
  }

  @Test
  void execute_unknownItemId_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> useCase.execute(new PurchaseShopItemUseCase.Request(
            session.getGameId(), "nonexistent-item")));
  }
}

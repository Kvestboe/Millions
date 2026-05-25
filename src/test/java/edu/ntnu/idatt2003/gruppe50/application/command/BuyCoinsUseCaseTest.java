package edu.ntnu.idatt2003.gruppe50.application.command;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BuyCoinsUseCaseTest {

  private GameSessionRepository repository;
  private BuyCoinsUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new BuyCoinsUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_playerReceivesCoins() {
    useCase.execute(new BuyCoinsUseCase.Request(session.getGameId(), 5));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(5, saved.getPlayer().getCoins());
  }

  @Test
  void execute_validRequest_moneyIsDeducted() {
    var moneyBefore = session.getPlayer().getMoney();

    useCase.execute(new BuyCoinsUseCase.Request(session.getGameId(), 1));

    GameSession saved = repository.findById(session.getGameId()).orElseThrow();
    assertTrue(saved.getPlayer().getMoney().compareTo(moneyBefore) < 0);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(new BuyCoinsUseCase.Request(UUID.randomUUID(), 5)));
  }

  @Test
  void execute_zeroCoins_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> useCase.execute(new BuyCoinsUseCase.Request(session.getGameId(), 0)));
  }
}

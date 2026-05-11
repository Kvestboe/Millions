package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.*;

public class StartGameSessionUseCaseTest {

  private GameSessionRepository repository;
  private StartGameSessionUseCase startSession;
  private Player player;
  private Exchange exchange;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    startSession = new StartGameSessionUseCase(repository);
    player = createDefaultPlayer();
    exchange = createDefaultExchange();
  }

  @Test
  void execute_validRequest_createsAndSavesSession() {
    StartGameSessionUseCase.Response response =
        startSession.execute(new StartGameSessionUseCase.Request(player, exchange));

    UUID gameId = response.gameId();
    assertNotNull(gameId);

    GameSession saved = repository.findById(gameId).orElseThrow();
    assertEquals(gameId, saved.getGameId());
    assertEquals(GameSessionState.ACTIVE, saved.getState());
    assertSame(player, saved.getPlayer());
    assertSame(exchange, saved.getExchange());
  }

  @Test
  void execute_nullPlayer_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> startSession.execute(new StartGameSessionUseCase.Request(null, exchange))
    );
  }

  @Test
  void execute_nullExchange_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> startSession.execute(new StartGameSessionUseCase.Request(player, null))
    );
  }

  @Test
  void execute_twoRequests_returnsDifferentGameIds() {
    UUID firstId = startSession.execute(new StartGameSessionUseCase.Request(player, exchange)).gameId();
    UUID secondId = startSession.execute(new StartGameSessionUseCase.Request(player, exchange)).gameId();

    assertNotEquals(firstId, secondId);
  }
}

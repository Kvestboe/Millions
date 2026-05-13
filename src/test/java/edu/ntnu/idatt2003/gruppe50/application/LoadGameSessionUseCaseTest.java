package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoadGameSessionUseCaseTest {

  private GameSessionRepository repository;
  private LoadGameSessionUseCase loadSession;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    loadSession = new LoadGameSessionUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_returnsIdAndState() {
    LoadGameSessionUseCase.Response response =
        loadSession.execute(new LoadGameSessionUseCase.Request(session.getGameId()));

    assertEquals(session.getGameId(), response.gameId());
    assertEquals(GameSessionState.ACTIVE, response.state());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unkownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> loadSession.execute(new LoadGameSessionUseCase.Request(unkownId)));
  }

  @Test
  void execute_finishedSession_throwsException() {
    session.finish();
    repository.save(session);

    LoadGameSessionUseCase.Response response =
        loadSession.execute(new LoadGameSessionUseCase.Request(session.getGameId()));

    assertEquals(session.getGameId(), response.gameId());
    assertEquals(GameSessionState.FINISHED, response.state());
  }

  @Test
  void execute_validRequest_updatesLastPlayed() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    GameSession existingSession =
        GameSession.rehydrate(
            UUID.randomUUID(),
            session.getPlayer(),
            session.getExchange(),
            GameSessionState.ACTIVE,
            LocalDate.now().minusDays(10),
            yesterday,
            List.of(bd(20)));
    repository.save(existingSession);

    assertEquals(yesterday, existingSession.getLastPlayed());

    loadSession.execute(new LoadGameSessionUseCase.Request(existingSession.getGameId()));

    GameSession repositorySession = repository.findById(existingSession.getGameId()).orElseThrow();
    assertEquals(LocalDate.now(), repositorySession.getLastPlayed());
  }
}

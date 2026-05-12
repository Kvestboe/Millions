package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.application.command.FinishGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FinishGameSessionUseCaseTest {

  private GameSessionRepository repository;
  private FinishGameSessionUseCase finishSession;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    finishSession = new FinishGameSessionUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_finishesSession() {
    assertEquals(GameSessionState.ACTIVE, session.getState());

    finishSession.execute(new FinishGameSessionUseCase.Request(session.getGameId()));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(GameSessionState.FINISHED, repositorySession.getState());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unknownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> finishSession.execute(new FinishGameSessionUseCase.Request(unknownId))
    );
  }
}

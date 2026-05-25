package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetAllSavesUseCaseTest {

  private GameSessionRepository repository;
  private GetAllSavesUseCase useCase;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetAllSavesUseCase(repository);
  }

  @Test
  void execute_emptyRepository_returnsEmptyList() {
    assertTrue(useCase.execute().isEmpty());
  }

  @Test
  void execute_activeSession_isFinishedFalse() {
    GameSession session = createDefaultGameSession();
    repository.save(session);

    assertFalse(useCase.execute().getFirst().isFinished());
  }

  @Test
  void execute_finishedSession_isFinishedTrue() {
    GameSession session = createDefaultGameSession();
    session.finish();
    repository.save(session);

    assertTrue(useCase.execute().getFirst().isFinished());
  }
}

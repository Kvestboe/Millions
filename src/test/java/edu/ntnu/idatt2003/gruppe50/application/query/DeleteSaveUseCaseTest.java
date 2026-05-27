package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.command.DeleteSaveUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteSaveUseCaseTest {

  private GameSessionRepository repository;
  private DeleteSaveUseCase useCase;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new DeleteSaveUseCase(repository);
  }

  @Test
  void execute_existingSession_isRemovedFromRepository() {
    GameSession session = createDefaultGameSession();
    repository.save(session);

    useCase.execute(session.getGameId());

    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void execute_nonExistentId_doesNotThrow() {
    assertDoesNotThrow(() -> useCase.execute(UUID.randomUUID()));
  }
}

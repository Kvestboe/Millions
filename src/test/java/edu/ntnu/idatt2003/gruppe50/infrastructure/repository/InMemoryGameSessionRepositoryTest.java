package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryGameSessionRepositoryTest {

  private InMemoryGameSessionRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
  }

  @Test
  void saveAndFindById_returnsSession() {
    GameSession session = createDefaultGameSession();

    repository.save(session);
    Optional<GameSession> repositorySession = repository.findById(session.getGameId());

    assertTrue(repositorySession.isPresent());
    assertSame(session, repositorySession.orElseThrow());
  }

  @Test
  void findById_unknownId_returnsEmpty() {
    Optional<GameSession> result = repository.findById(UUID.randomUUID());

    assertTrue(result.isEmpty());
  }

  @Test
  void save_nullSession_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> repository.save(null));
  }

  @Test
  void findById_nullId_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> repository.findById(null));
  }
}

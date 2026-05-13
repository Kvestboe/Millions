package edu.ntnu.idatt2003.gruppe50.application;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.command.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionFinishedException;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdvanceWeekUseCaseTest {

  private GameSessionRepository repository;
  private AdvanceWeekUseCase advanceWeek;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    advanceWeek = new AdvanceWeekUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_validRequest_advancesWeek() {
    int week = session.getExchange().getWeek();

    advanceWeek.execute(new AdvanceWeekUseCase.Request(session.getGameId()));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(week + 1, repositorySession.getExchange().getWeek());
  }

  @Test
  void execute_invalidSession_throwsException() {
    UUID unknownId = UUID.randomUUID();

    assertThrows(
        GameSessionNotFoundException.class,
        () -> advanceWeek.execute(new AdvanceWeekUseCase.Request(unknownId)));
  }

  @Test
  void execute_finishedSession_throwsExceptionAndKeepOriginal() {
    session.finish();
    int week = session.getExchange().getWeek();

    assertThrows(
        GameSessionFinishedException.class,
        () -> advanceWeek.execute(new AdvanceWeekUseCase.Request(session.getGameId())));

    GameSession repositorySession = repository.findById(session.getGameId()).orElseThrow();
    assertEquals(week, repositorySession.getExchange().getWeek());
  }
}

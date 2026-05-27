package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StatusProgressDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetStatusProgressUseCaseTest {

  private GameSessionRepository repository;
  private GetStatusProgressUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetStatusProgressUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(UUID.randomUUID()));
  }

  @Test
  void execute_defaultPlayer_isNovice() {
    StatusProgressDto dto = useCase.execute(session.getGameId());

    assertEquals("Novice", dto.currentStatus());
  }

  @Test
  void execute_defaultPlayer_nextStatusIsInvestor() {
    StatusProgressDto dto = useCase.execute(session.getGameId());

    assertEquals("Investor", dto.nextStatus());
  }

  @Test
  void execute_defaultPlayer_isNotAtMaxLevel() {
    StatusProgressDto dto = useCase.execute(session.getGameId());

    assertFalse(dto.atMaxLevel());
  }

  @Test
  void execute_returnsProgressBetweenZeroAndOne() {
    StatusProgressDto dto = useCase.execute(session.getGameId());

    assertTrue(dto.progress() >= 0.0);
    assertTrue(dto.progress() <= 1.0);
  }

  @Test
  void execute_returnsNonNegativeGap() {
    StatusProgressDto dto = useCase.execute(session.getGameId());

    assertTrue(dto.weeksRemaining() >= 0);
    assertTrue(dto.moneyRemaining().signum() >= 0);
  }
}

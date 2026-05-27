package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.GoalProgressDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetGoalProgressUseCaseTest {

  private GameSessionRepository repository;
  private GetGoalProgressUseCase useCase;
  private GameSession session;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGameSessionRepository();
    useCase = new GetGoalProgressUseCase(repository);
    session = createDefaultGameSession();
    repository.save(session);
  }

  @Test
  void execute_unknownSession_throwsGameSessionNotFoundException() {
    assertThrows(GameSessionNotFoundException.class,
        () -> useCase.execute(UUID.randomUUID()));
  }

  @Test
  void execute_defaultPlayer_returnsCurrentNetWorth() {
    GoalProgressDto dto = useCase.execute(session.getGameId());

    // Default player har 10 000 kr i cash, ingen aksjer
    assertEquals(0, bd(10000).compareTo(dto.currentNetWorth()));
  }

  @Test
  void execute_goalIsOneMillion() {
    GoalProgressDto dto = useCase.execute(session.getGameId());

    assertEquals(0, bd(1000000).compareTo(dto.goal()));
  }

  @Test
  void execute_progressIsRatioOfNetWorthToGoal() {
    GoalProgressDto dto = useCase.execute(session.getGameId());

    // 10 000 / 1 000 000 = 0.01
    assertEquals(0.01, dto.progress(), 0.0001);
  }

  @Test
  void execute_progressClampedAtOne_whenNetWorthExceedsGoal() {
    session.getPlayer().addMoney(bd(2_000_000));

    GoalProgressDto dto = useCase.execute(session.getGameId());

    assertEquals(1.0, dto.progress(), 0.0001);
  }
}
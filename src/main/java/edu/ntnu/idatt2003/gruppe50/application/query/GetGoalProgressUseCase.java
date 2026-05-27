package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.GoalProgressDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Use case for fetching the player's progress towards the win threshold.
 */
public final class GetGoalProgressUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates a new get goal progress use case.
   *
   * @param repository the repository used to load game sessions
   */
  public GetGoalProgressUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns the goal progress for the given game session.
   *
   * @param gameId the id of the game session
   * @return the goal progress
   * @throws GameSessionNotFoundException if the game session does not exist
   */
  public GoalProgressDto execute(UUID gameId) {
    GameSession session = repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new);

    BigDecimal current = session.getPlayer().getNetWorth();
    BigDecimal goal = GameSession.WIN_THRESHOLD;

    double progress = current.divide(goal, 6, RoundingMode.HALF_UP).doubleValue();
    progress = Math.min(1.0, Math.max(0.0, progress));

    return new GoalProgressDto(current, goal, progress);
  }
}

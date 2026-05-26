package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StatusProgressDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.LevelGap;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case for fetching the player's progress towards the next status level.
 */
public final class GetStatusProgressUseCase {

  private final GameSessionRepository repository;

  public GetStatusProgressUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public StatusProgressDto execute(UUID gameId) {
    GameSession session = repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new);

    Player player = session.getPlayer();
    Status current = player.getStatus();
    Status next = current.next();

    // Edge case: spilleren er på toppstatus (SPECULATOR)
    if (next == null) {
      return new StatusProgressDto(
          current.displayName(),
          null,
          1.0,
          0,
          BigDecimal.ZERO,
          true
      );
    }

    double progress = player.getProgressToNextLevel();
    Optional<LevelGap> gap = player.getGapToNextLevel();

    int weeks = gap.map(LevelGap::weeksRemaining).orElse(0);
    BigDecimal money = gap.map(LevelGap::moneyRemaining).orElse(BigDecimal.ZERO);

    return new StatusProgressDto(
        current.displayName(),
        next.displayName(),
        progress,
        weeks,
        money,
        false
    );
  }
}
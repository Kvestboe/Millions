package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.UUID;

/** Advances game session by one week and saves the change. */
public final class AdvanceWeekUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public AdvanceWeekUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Output from advancing the game by one week.
   *
   * @param outcome current game outcome after the week has advanced
   */
  public record Result(GameOutcome outcome) {}

  /**
   * Executes the use case for a given game id.
   *
   * @param request input request containing game id
   * @return result containing the evaluated game outcome after advancing the week
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Result execute(Request request) {
    GameSession session =
        repository.findById(request.gameId).orElseThrow(GameSessionNotFoundException::new);

    session.advanceWeek();
    repository.save(session);
    return new Result(session.evaluateOutcome());
  }

  /**
   * Input for advancing a session week.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}
}

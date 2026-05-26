package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;

import java.util.Comparator;
import java.util.List;

/** Retrieves summaries for all saved game sessions. */
public final class GetAllSavesUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load saved game sessions
   */
  public GetAllSavesUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns all saved sessions sorted by most recently played.
   *
   * @return list of save summaries
   */
  public List<SaveSummaryDto> execute()  {
    return repository.findAll().stream()
        .map(s -> new SaveSummaryDto(
            s.getGameId(),
            s.getPlayer().getName(),
            s.getExchange().getWeek(),
            s.getPlayer().getStatus().displayName(),
            s.getPlayer().getNetWorth(),
            s.getLastPlayed(),
            s.getState() == GameSessionState.FINISHED
        )).sorted(Comparator.comparing(SaveSummaryDto::lastPlayed).reversed())
        .toList();
  }
}

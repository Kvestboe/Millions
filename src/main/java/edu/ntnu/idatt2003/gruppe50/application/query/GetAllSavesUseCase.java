package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;

public final class GetAllSavesUseCase {

  private final GameSessionRepository repository;

  public GetAllSavesUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public List<SaveSummaryDto> execute()  {
    return repository.findAll().stream()
        .map(s -> new SaveSummaryDto(
            s.getGameId(),
            s.getPlayer().getName(),
            s.getExchange().getWeek(),
            s.getPlayer().getStatus(),
            s.getPlayer().getNetWorth(),
            s.getLastPlayed(),
            s.getState() == GameSessionState.FINISHED
        )).toList();
  }
}

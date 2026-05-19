package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.Optional;
import java.util.UUID;

public class GetStockDetailUseCase {

  private final GameSessionRepository repository;

  public GetStockDetailUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Optional<StockDto> execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    return session.getExchange().getStocks().stream()
        .filter(s -> s.getSymbol().equals(request.symbol()))
        .findFirst()
        .map(DtoMapper::createStockDetail);
  }

  public record Request(UUID gameId, String symbol) {}
}

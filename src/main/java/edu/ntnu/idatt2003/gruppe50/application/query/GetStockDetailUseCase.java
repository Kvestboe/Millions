package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Retrieves detailed stock data for a game session.
 */
public class GetStockDetailUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetStockDetailUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Finds detailed data for one stock in the requested game session.
   *
   * @param request input with game id and stock symbol
   * @return optional stock details, empty if the symbol does not exist
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Optional<StockDto> execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    return session.getExchange().getStocks().stream()
        .filter(s -> s.getSymbol().equals(request.symbol()))
        .findFirst()
        .map(DtoMapper::createStockDetail);
  }

  /**
   * Input for retrieving stock details.
   *
   * @param gameId id of the game session
   * @param symbol stock symbol to retrieve details for
   */
  public record Request(UUID gameId, String symbol) {
  }
}

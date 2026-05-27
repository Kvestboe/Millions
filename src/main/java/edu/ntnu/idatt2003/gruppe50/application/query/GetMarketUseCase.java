package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;
import java.util.UUID;

/**
 * Retrieves market stock data for a game session.
 */
public class GetMarketUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetMarketUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Retrieves all stocks, or filtered stocks when a search query is provided.
   *
   * @param request input with game id and optional search query
   * @return response containing matching stock DTOs
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    List<Stock> stocks = request.query() == null || request.query().isBlank()
        ? session.getExchange().getStocks()
        : session.getExchange().findStocks(request.query());

    List<StockDto> stockDtos = stocks.stream()
        .map(DtoMapper::createStockDto)
        .toList();

    return new Response(stockDtos);
  }

  /**
   * Input for retrieving market data.
   *
   * @param gameId id of the game session
   * @param query  optional search query for filtering stocks
   */
  public record Request(UUID gameId, String query) {
  }

  /**
   * Output from retrieving market data.
   *
   * @param stocks matching stocks
   */
  public record Response(List<StockDto> stocks) {
  }
}

package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.WeeklyMoversDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;
import java.util.UUID;

/**
 * Use case for fetching the top weekly gainers and losers.
 */
public final class GetWeeklyMoversUseCase {

  private static final int DEFAULT_LIMIT = 3;

  private final GameSessionRepository repository;

  public GetWeeklyMoversUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns the top {@code limit} gainers and losers for the given game session.
   *
   * @param gameId the id of the game session
   * @param limit how many stocks per category
   * @return the weekly movers
   * @throws GameSessionNotFoundException if the game session does not exist
   */
  public WeeklyMoversDto execute(UUID gameId, int limit) {
    GameSession session = repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new);
    Exchange exchange = session.getExchange();

    List<StockDto> gainers = exchange.getGainers(limit).stream()
        .map(DtoMapper::createStockDto)
        .toList();

    List<StockDto> losers = exchange.getLosers(limit).stream()
        .map(DtoMapper::createStockDto)
        .toList();

    return new WeeklyMoversDto(gainers, losers);
  }

  /**
   * Returns the top 3 gainers and losers (default limit).
   *
   * @param gameId the id of the game session
   * @return the weekly movers
   */
  public WeeklyMoversDto execute(UUID gameId) {
    return execute(gameId, DEFAULT_LIMIT);
  }
}

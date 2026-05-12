package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Portfolio;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Retrieves portfolio-related data for a game session.
 */
public final class GetPortfolioUseCase {
  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetPortfolioUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Builds a portfolio response for the requested session.
   *
   * @param request input containing game id
   * @return portfolio response including totals and holdings
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    Player player = session.getPlayer();
    Portfolio portfolio = player.getPortfolio();

    BigDecimal cash = player.getMoney();
    BigDecimal portfolioValue = portfolio.getNetWorth(); // the value of all shares
    BigDecimal netWorth = player.getNetWorth(); // the value of all player assets

    List<ShareDto> shares = portfolio.getShares().stream()
        .map(DtoMapper::createShareDto)
        .toList();

    List<BigDecimal> netWorthHistory = session.getNetWorthHistory();

    return new Response(cash, portfolioValue, netWorth, shares, netWorthHistory);
  }

  /**
   * Input for retrieving portfolio data.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}

  /**
   * Portfolio aggregate response for the UI layer.
   *
   * @param cash available player cash
   * @param portfolioValue current portfolio liquidation value
   * @param netWorth total net worth (cash + portfolio)
   * @param shares current owned shares as DTOs
   */
  public record Response(
      BigDecimal cash,
      BigDecimal portfolioValue,
      BigDecimal netWorth,
      List<ShareDto> shares,
      List<BigDecimal> netWorthHistory
  ) {}

}

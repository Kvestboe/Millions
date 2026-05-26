package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PortfolioDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Portfolio;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Retrieves portfolio-related data for a game session. */
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
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    Player player = session.getPlayer();
    Portfolio portfolio = player.getPortfolio();

    BigDecimal cash = player.getMoney();
    BigDecimal portfolioValue = portfolio.getNetWorth(); // the value of all shares
    BigDecimal netWorth = player.getNetWorth(); // the value of all player assets

    List<ShareDto> shares = portfolio.getShares().stream()
        .collect(Collectors.groupingBy(share -> share.getStock().getSymbol()))
        .values()
        .stream()
        .map(DtoMapper::createAggregatedShareDto)
        .sorted(Comparator.comparing(ShareDto::symbol))
        .toList();

    List<BigDecimal> netWorthHistory = session.getNetWorthHistory();

    var archive = player.getTransactionArchive().getTransactions();
    Set<Integer> buyWeeks  = archive.stream().filter(t ->  t instanceof Purchase).map(t -> t.getWeek()).collect(Collectors.toSet());
    Set<Integer> sellWeeks = archive.stream().filter(t -> !(t instanceof Purchase)).map(t -> t.getWeek()).collect(Collectors.toSet());

    return new Response(new PortfolioDto(cash, portfolioValue, netWorth, shares, netWorthHistory, buyWeeks, sellWeeks));
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
   * @param portfolio portfolio data for the requested game session
   */
  public record Response(PortfolioDto portfolio) {}
}

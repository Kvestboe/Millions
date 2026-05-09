package edu.ntnu.idatt2003.gruppe50.application;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Portfolio;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class GetPortfolioUseCase {
  private final GameSessionRepository repository;

  public GetPortfolioUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    Player player = session.getPlayer();
    Portfolio portfolio = player.getPortfolio();

    BigDecimal cash = player.getMoney();
    BigDecimal portfolioValue = portfolio.getNetWorth(); // the value of all shares
    BigDecimal netWorth = player.getNetWorth(); // the value of all player assets

    List<ShareDto> shares = portfolio.getShares().stream()
        .map(share -> new ShareDto(
            share.getShareId(),
            share.getStock().getSymbol(),
            share.getStock().getCompany(),
            share.getQuantity(),
            share.getPurchasePrice(),
            share.getStock().getSalesPrice(),
            share.getStock().getSalesPrice().multiply(share.getQuantity())
        ))
        .toList();

    return new Response(cash, portfolioValue, netWorth, shares);
  }

  public record Request(UUID gameId) {}

  public record Response(
      BigDecimal cash,
      BigDecimal portfolioValue,
      BigDecimal netWorth,
      List<ShareDto> shares
  ) {}

  public record ShareDto(
      UUID shareId,
      String symbol,
      String stock,
      BigDecimal quantity,
      BigDecimal purchasePrice,
      BigDecimal currentPrice,
      BigDecimal currentShareValue
  ) {}

}

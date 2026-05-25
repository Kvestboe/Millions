package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Sells an owned share inside a game session and saves updated state. */
public final class SellShareUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public SellShareUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a sell operation for an existing game session.
   *
   * @param request input with game id and owned share id
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    List<Transaction> txs = session.sell(request.symbol(), request.quantity());
    repository.save(session);

    BigDecimal totalAmount = txs.stream()
        .map(tx -> tx.getCalculator().calculateTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal newHolding = session.getPlayer().getPortfolio()
        .getShares(request.symbol()).stream()
        .map(Share::getQuantity)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new Response(
        request.symbol(),
        request.quantity(),
        totalAmount,
        newHolding,
        txs.getFirst().getWeek()
    );
  }

  /**
   * Input for selling one share in a session.
   *
   * @param gameId id of the game session
   * @param shareId id of the owned share to sell
   */
  public record Request(UUID gameId, String symbol, BigDecimal quantity) {}

  public record Response(
      String symbol,
      BigDecimal quantity,
      BigDecimal totalAmount,
      BigDecimal newHoldingQuantity,
      int week
  ) {}
}

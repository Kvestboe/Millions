package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Buys shares inside a game session and saves the updated state.
 */
public final class BuyShareUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public BuyShareUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a buy operation for an existing game session.
   *
   * @param request input with game id, symbol and quantity
   * @return response with purchase receipt data
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId())
        .orElseThrow(GameSessionNotFoundException::new);

    Transaction tx = session.buy(request.symbol(), request.quantity());
    repository.save(session);

    BigDecimal newHolding = session.getPlayer().getPortfolio()
        .getShares(request.symbol()).stream()
        .map(Share::getQuantity)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new Response(
        request.symbol(),
        request.quantity(),
        tx.getCalculator().calculateTotal(),
        newHolding,
        tx.getWeek()
    );
  }

  /**
   * Input for buying shares in a session.
   *
   * @param gameId   id of the game session
   * @param symbol   stock symbol to buy
   * @param quantity quantity to buy
   */
  public record Request(UUID gameId, String symbol, BigDecimal quantity) {
  }

  /**
   * Output from a completed buy operation.
   *
   * @param symbol             stock symbol that was bought
   * @param quantity           quantity that was bought
   * @param totalAmount        total amount paid including fees
   * @param newHoldingQuantity updated owned quantity of the stock
   * @param week               week when the purchase was completed
   */
  public record Response(
      String symbol,
      BigDecimal quantity,
      BigDecimal totalAmount,
      BigDecimal newHoldingQuantity,
      int week
  ) {
  }
}

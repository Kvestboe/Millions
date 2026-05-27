package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Places a buy limit order inside a game session and saves the updated state.
 */
public final class PlaceBuyLimitOrderUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public PlaceBuyLimitOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a buy limit order placement for an existing game session.
   *
   * @param request input with game id, symbol, quantity, target price and duration
   * @return response with order receipt data
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    LimitBuyOrder order = session.placeBuyLimitOrder(
        request.symbol(), request.quantity(),
        request.targetPrice(), request.duration()
    );
    repository.save(session);

    return new Response(
        request.symbol(),
        request.quantity(),
        request.targetPrice(),
        order.getCreatedWeek(),
        order.getExpiryWeek()
    );
  }

  /**
   * Input for placing a buy limit order in a session.
   *
   * @param gameId      id of the game session
   * @param symbol      stock symbol to buy
   * @param quantity    quantity to buy
   * @param targetPrice maximum price the player is willing to buy at
   * @param duration    number of weeks the order should stay active
   */
  public record Request(
      UUID gameId,
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {
  }

  /**
   * Output from placing a buy limit order.
   *
   * @param symbol        stock symbol for the order
   * @param quantity      order quantity
   * @param targetPrice   target buy price
   * @param placedAtWeek  week when the order was placed
   * @param expiresAtWeek week when the order expires
   */
  public record Response(
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int placedAtWeek,
      int expiresAtWeek
  ) {
  }
}

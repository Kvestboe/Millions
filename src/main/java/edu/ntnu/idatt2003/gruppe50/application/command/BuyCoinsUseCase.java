package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.shop.CoinExchange;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItemFactory;

/** Buys shop coins inside a game session and saves the updated state. */
public final class BuyCoinsUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public BuyCoinsUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a coin purchase for an existing game session.
   *
   * @param request input with game id and coin amount
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public void execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    Shop shop = new Shop(
        session.getCoinExchange(),
        ShopItemFactory.createDefaultItems()
    );

    shop.buyCoins(session.getPlayer(), request.coins());
    repository.save(session);
  }

  /**
   * Input for buying shop coins in a session.
   *
   * @param gameId id of the game session
   * @param coins number of coins to buy
   */
  public record Request(java.util.UUID gameId, int coins) {}
}
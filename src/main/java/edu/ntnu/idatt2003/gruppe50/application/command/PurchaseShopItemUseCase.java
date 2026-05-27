package edu.ntnu.idatt2003.gruppe50.application.command;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.shop.CoinExchange;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItemFactory;

/**
 * Purchases or applies a shop item inside a game session and saves the updated state.
 */
public final class PurchaseShopItemUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load and save game sessions
   */
  public PurchaseShopItemUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Executes a shop item purchase or applies an already owned item.
   *
   * @param request input with game id and shop item id
   * @throws GameSessionNotFoundException if the session does not exist
   * @throws InsufficientCoinsException   if the player does not have enough coins
   * @throws IllegalArgumentException     if the item cannot be purchased or applied
   */
  public void execute(Request request) throws InsufficientCoinsException {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    Shop shop = new Shop(
        new CoinExchange(session.getPlayer().getStartingMoney()),
        ShopItemFactory.createDefaultItems()
    );

    shop.purchaseItem(session.getPlayer(), request.itemId(), session.getDifficulty());
    repository.save(session);
  }

  /**
   * Input for purchasing or applying a shop item in a session.
   *
   * @param gameId id of the game session
   * @param itemId id of the shop item
   */
  public record Request(java.util.UUID gameId, String itemId) {
  }
}
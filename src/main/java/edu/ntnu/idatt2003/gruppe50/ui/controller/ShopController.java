package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyCoinsUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PurchaseShopItemUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItem;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller for shop actions and shop data shown in the user interface.
 */
public class ShopController {

  private final UUID gameId;
  private final Shop shop;
  private final Player player;
  private final Difficulty difficulty;
  private final BuyCoinsUseCase buyCoins;
  private final PurchaseShopItemUseCase purchaseShopItem;

  /**
   * Creates a new shop controller.
   *
   * @param gameId the id of the game session this shop belongs to
   * @param shop the shop used for item and coin metadata
   * @param player the player using the shop
   * @param difficulty the current game difficulty
   * @param buyCoins use case for buying coins and saving the session
   * @param purchaseShopItem use case for purchasing or applying shop items
   *     and saving the session
   * @throws IllegalArgumentException if any argument is null
   */
  public ShopController(
      UUID gameId,
      Shop shop,
      Player player,
      Difficulty difficulty,
      BuyCoinsUseCase buyCoins,
      PurchaseShopItemUseCase purchaseShopItem
  ) {
    Validate.notNull(gameId, "Game id");
    Validate.notNull(shop, "Shop");
    Validate.notNull(player, "Player");
    Validate.notNull(difficulty, "Difficulty");
    Validate.notNull(buyCoins, "Buy coins use case");
    Validate.notNull(purchaseShopItem, "Purchase shop item use case");

    this.gameId = gameId;
    this.shop = shop;
    this.player = player;
    this.difficulty = difficulty;
    this.buyCoins = buyCoins;
    this.purchaseShopItem = purchaseShopItem;
  }

  /**
   * Returns the items available in the shop.
   *
   * @return a copy of the shop items
   */
  public List<ShopItem> getItems() {
    return List.copyOf(shop.getItems());
  }

  /**
   * Returns the current price for one coin.
   *
   * @return the current coin price
   */
  public BigDecimal getCurrentCoinPrice() {
    return shop.getCoinExchange().getCurrentPricePerCoin();
  }

  /**
   * Attempts to buy coins for the player.
   *
   * @param coins the number of coins to buy
   * @return a message describing whether the purchase succeeded or failed
   */
  public String buyCoins(int coins) {
    try {
      buyCoins.execute(new BuyCoinsUseCase.Request(gameId, coins));
      return "Coins purchased.";
    } catch (IllegalArgumentException e) {
      return e.getMessage();
    }
  }

  /**
   * Attempts to purchase a shop item or apply it if it is already owned.
   *
   * @param itemId the id of the item to buy or apply
   * @return a message describing whether the purchase/application succeeded or failed
   */
  public String purchaseItem(String itemId) {
    try {
      purchaseShopItem.execute(new PurchaseShopItemUseCase.Request(gameId, itemId));
      return "Item purchased.";
    } catch (IllegalArgumentException | InsufficientCoinsException e) {
      return e.getMessage();
    }
  }

  /**
   * Returns the player's current coin balance.
   *
   * @return the player's coins
   */
  public int getPlayerCoins() {
    return player.getCoins();
  }

  /**
   * Returns the player's current money balance.
   *
   * @return the player's money
   */
  public BigDecimal getPlayerMoney() {
    return player.getMoney();
  }

  /**
   * Returns the id of the player's active theme.
   *
   * @return the active theme id
   */
  public String getActiveTheme() {
    return player.getActiveTheme();
  }

  /**
   * Returns the current game difficulty.
   *
   * @return the current difficulty
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Checks whether the player owns the theme with the given id.
   *
   * @param themeId the theme id to check
   * @return true if the player owns the theme, false otherwise
   */
  public boolean ownsTheme(String themeId) {
    return player.ownsTheme(themeId);
  }

  /**
   * Returns the coin price history.
   *
   * @return coin price history
   */
  public List<BigDecimal> getCoinPriceHistory() {
    return shop.getCoinExchange().getPriceHistory();
  }

  /** Advances the coin exchange price by one week. */
  public void advanceCoinExchange() {
    shop.getCoinExchange().advanceShop();
  }
}
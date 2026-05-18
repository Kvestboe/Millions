package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.domain.shop.Shop;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItem;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for shop actions and shop data shown in the user interface.
 */
public class ShopController {

  private final Shop shop;
  private final Player player;
  private final Difficulty difficulty;

  /**
   * Creates a new shop controller.
   *
   * @param shop the shop used for purchases
   * @param player the player using the shop
   * @param difficulty the current game difficulty
   * @throws IllegalArgumentException if any argument is null
   */
  public ShopController(Shop shop, Player player, Difficulty difficulty) {
    Validate.notNull(shop, "Shop");
    Validate.notNull(player, "Player");
    Validate.notNull(difficulty, "Difficulty");

    this.shop = shop;
    this.player = player;
    this.difficulty = difficulty;
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
      shop.buyCoins(player, coins);
      return "Coins purchased.";
    } catch (IllegalArgumentException e) {
      return e.getMessage();
    }
  }

  /**
   * Attempts to purchase a shop item for the player.
   *
   * @param itemId the id of the item to buy
   * @return a message describing whether the purchase succeeded or failed
   */
  public String purchaseItem(String itemId) {
    try {
      shop.purchaseItem(player, itemId, difficulty);
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
}
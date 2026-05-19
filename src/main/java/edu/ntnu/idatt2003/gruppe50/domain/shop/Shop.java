package edu.ntnu.idatt2003.gruppe50.domain.shop;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.shop.items.ThemeItem;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the shop where players can buy coins and shop items.
 */
public class Shop {

  private final CoinExchange coinExchange;
  private final List<ShopItem> items;

  /**
   * Creates a new shop with the given coin exchange and available items.
   *
   * @param coinExchange the exchange used to buy coins
   * @param items the items available in the shop
   * @throws IllegalArgumentException if {@code coinExchange} is null or {@code items} is empty
   */
  public Shop(CoinExchange coinExchange, List<ShopItem> items) {
    Validate.notNull(coinExchange, "Coin exchange");
    Validate.notEmpty(items, "Items");

    this.coinExchange = coinExchange;
    this.items = List.copyOf(items);
  }

  /**
   * Returns the coin exchange used by the shop.
   *
   * @return the coin exchange
   */
  public CoinExchange getCoinExchange() {
    return coinExchange;
  }

  /**
   * Returns the items available in the shop.
   *
   * @return a copy of the available items
   */
  public List<ShopItem> getItems() {
    return new ArrayList<>(items);
  }

  /**
   * Buys coins for the player using the current coin exchange price.
   *
   * @param player the player buying coins
   * @param coins the number of coins to buy
   * @throws IllegalArgumentException if the player is null, coins is not positive,
   *     or the player does not have enough money
   */
  public void buyCoins(Player player, int coins) {
    Validate.notNull(player, "Player");
    Validate.positiveInt(coins, "Coins");

    BigDecimal cost = coinExchange.calculateCost(coins);
    if (player.getMoney().compareTo(cost) < 0) {
      throw new IllegalArgumentException("Not enough money to buy coins");
    }

    player.withdrawMoney(cost);
    player.addCoins(coins);
  }

  /**
   * Purchases an item for the player and applies it after payment succeeds.
   *
   * @param player the player buying the item
   * @param itemId the id of the item to buy
   * @param difficulty the current game difficulty
   * @throws InsufficientCoinsException if the player does not have enough coins
   * @throws IllegalArgumentException if any input is invalid, the item does not exist,
   *     or the player does not meet the net worth requirement
   */
  public void purchaseItem(Player player, String itemId, Difficulty difficulty)
      throws InsufficientCoinsException {
    Validate.notNull(player, "Player");
    Validate.notBlank(itemId, "Item id");
    Validate.notNull(difficulty, "Difficulty");

    ShopItem item = findItemById(itemId);

    if (item instanceof ThemeItem themeItem && player.ownsTheme(themeItem.getThemeId())) {
      player.setActiveTheme(themeItem.getThemeId());
      return;
    }

    if (player.getNetWorth().compareTo(item.getRequiredNetWorth()) < 0) {
      throw new IllegalArgumentException("Not enough net worth to buy this item");
    }

    int price = item.getPrice(difficulty);
    player.spendCoins(price);
    item.applyTo(player);
  }

  private ShopItem findItemById(String itemId) {
    return items.stream()
        .filter(item -> item.getId().equals(itemId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown shop item: " + itemId));
  }
}

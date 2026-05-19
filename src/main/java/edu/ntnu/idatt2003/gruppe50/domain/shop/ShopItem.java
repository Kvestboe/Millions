package edu.ntnu.idatt2003.gruppe50.domain.shop;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;

import java.math.BigDecimal;

/**
 * Represents an item that can be bought in the shop.
 */
public interface ShopItem {

  /**
   * Returns the unique id of the shop item.
   *
   * @return the item id
   */
  String getId();

  /**
   * Returns the display name of the shop item.
   *
   * @return the item name
   */
  String getName();

  /**
   * Returns the description of the shop item.
   *
   * @return the item description
   */
  String getDescription();

  /**
   * Returns the price of the item in coins.
   *
   * @param difficulty the game difficulty
   * @return the coin price
   */
  int getPrice(Difficulty difficulty);

  /**
   * Returns the required networth for the item.
   *
   * @return the required networth
   */
  BigDecimal getRequiredNetWorth();

  /**
   * Applies the item effect to the player or game.
   *
   * @param player the player receiving the item effect
   */
  void applyTo(Player player);
}
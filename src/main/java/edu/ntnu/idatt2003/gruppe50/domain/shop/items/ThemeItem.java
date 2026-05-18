package edu.ntnu.idatt2003.gruppe50.domain.shop.items;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.shop.ShopItem;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a shop item that activates a visual theme for the player.
 */
public class ThemeItem implements ShopItem {

  private final String id;
  private final String name;
  private final String description;
  private final Map<Difficulty, Integer> prices;
  private final String themeId;
  private final BigDecimal requiredNetWorth;

  /**
   * Creates a new theme item.
   *
   * @param id the unique shop item id
   * @param name the display name of the theme item
   * @param description the description shown in the shop
   * @param themeId the id of the theme this item activates
   * @param prices the item prices by difficulty
   * @param requiredNetWorth the minimum net worth required to buy this theme
   * @throws IllegalArgumentException if any required value is invalid
   */
  public ThemeItem(
      String id,
      String name,
      String description,
      String themeId,
      Map<Difficulty, Integer> prices,
      BigDecimal requiredNetWorth
  ) {
    Validate.notBlank(id, "Id");
    Validate.notBlank(name, "Name");
    Validate.notBlank(description, "Description");
    Validate.notBlank(themeId, "Theme id");
    Validate.notNull(prices, "Prices");
    Validate.notNull(requiredNetWorth, "Required net worth");

    if (requiredNetWorth.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Required net worth cannot be negative");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.themeId = themeId;
    this.prices = new EnumMap<>(prices);
    this.requiredNetWorth = requiredNetWorth;

  }

  /**
   * Returns the unique id of the shop item.
   *
   * @return the item id
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Returns the display name of the theme item.
   *
   * @return the item name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Returns the description of the theme item.
   *
   * @return the item description
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Returns the price of this theme item for the given difficulty.
   *
   * @param difficulty the game difficulty
   * @return the price in coins
   * @throws IllegalArgumentException if {@code difficulty} is {@code null}
   * @throws IllegalStateException if no price exists for the difficulty
   */
  @Override
  public int getPrice(Difficulty difficulty) {
    Validate.notNull(difficulty, "Difficulty");

    Integer price = prices.get(difficulty);

    if (price == null) {
      throw new IllegalStateException("Missing price for difficulty: " + difficulty);
    }

    return price;
  }

  /**
   * Returns the id of the theme this item activates.
   *
   * @return the theme id
   */
  public String getThemeId() {
    return themeId;
  }

  /**
   * Returns the minimum net worth required to buy this theme.
   *
   * @return the required net worth
   */
  public BigDecimal getRequiredNetWorth() {
    return requiredNetWorth;
  }

  /**
   * Applies the theme item effect to the player.
   *
   * @param player the player receiving the theme
   * @throws IllegalArgumentException if {@code player} is {@code null}
   */
  @Override
  public void applyTo(Player player) {
    Validate.notNull(player, "Player");
    player.setActiveTheme(themeId);
  }
}

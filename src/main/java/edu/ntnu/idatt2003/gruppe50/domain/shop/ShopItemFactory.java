package edu.ntnu.idatt2003.gruppe50.domain.shop;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.shop.items.ThemeItem;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating the default shop items used by the game.
 */
public class ShopItemFactory {

  private ShopItemFactory() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Creates the default list of shop items.
   *
   * @return the default shop items
   */
  public static List<ShopItem> createDefaultItems() {
    return List.of(
        createThemeItem(
            "theme-default",
            "Default Dark",
            "The original dark market theme.",
            "default",
            0,
            BigDecimal.ZERO
        ),
        createThemeItem(
            "theme-midnight-black",
            "Midnight Black",
            "A dark market terminal theme.",
            "midnight-black",
            15,
            new BigDecimal("0")
        ),
        createThemeItem(
            "theme-forest-fund",
            "Forest Fund",
            "A calm green trading desk theme.",
            "forest-fund",
            35,
            new BigDecimal("50000")
        ),
        createThemeItem(
          "theme-vintage-cream",
          "Vintage Cream",
          "A vintage cream theme.",
          "vintage-cream",
          50,
          new BigDecimal("100000")
        ),
        createThemeItem(
            "theme-copper-shadow",
            "Copper Shadow",
            "A warm copper and shadow theme.",
            "copper-shadow",
            70,
            new BigDecimal("200000")
        ),
        createThemeItem(
            "theme-royal-gold",
            "Royal Gold",
            "A premium gold portfolio theme.",
            "royal-gold",
            120,
            new BigDecimal("800000")
        )
    );
  }

  private static ShopItem createThemeItem(
      String id,
      String name,
      String description,
      String themeId,
      int price,
      BigDecimal requiredNetWorth
  ) {
    return new ThemeItem(
        id,
        name,
        description,
        themeId,
        createPrices(price),
        requiredNetWorth
    );
  }

  private static Map<Difficulty, Integer> createPrices(int price) {
    Map<Difficulty, Integer> prices = new EnumMap<>(Difficulty.class);
    prices.put(Difficulty.EASY, price);
    prices.put(Difficulty.MEDIUM, price);
    prices.put(Difficulty.HARD, price);
    return prices;
  }
}
package edu.ntnu.idatt2003.gruppe50.domain.shop;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ShopTest {

  private Shop shop;
  private Player player;

  @BeforeEach
  void setUp() {
    shop = new Shop(new CoinExchange(BigDecimal.valueOf(10_000)), ShopItemFactory.createDefaultItems());
    player = new Player("Tester", BigDecimal.valueOf(10_000));
  }

  @Test
  void constructor_nullCoinExchange_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Shop(null, ShopItemFactory.createDefaultItems()));
  }

  @Test
  void constructor_emptyItems_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> new Shop(new CoinExchange(BigDecimal.valueOf(10_000)), List.of()));
  }

  @Nested
  class BuyCoinsTests {

    @Test
    void buyCoins_validPurchase_deductsMoney() {
      BigDecimal costBefore = player.getMoney();
      shop.buyCoins(player, 1);
      assertTrue(player.getMoney().compareTo(costBefore) < 0);
    }

    @Test
    void buyCoins_validPurchase_addsCoinsToPlayer() {
      shop.buyCoins(player, 5);
      assertEquals(5, player.getCoins());
    }

    @Test
    void buyCoins_insufficientMoney_throwsException() {
      Player broke = new Player("Broke", BigDecimal.valueOf(1));
      assertThrows(IllegalArgumentException.class, () -> shop.buyCoins(broke, 1000));
    }

    @Test
    void buyCoins_nullPlayer_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> shop.buyCoins(null, 1));
    }

    @Test
    void buyCoins_zeroCoins_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> shop.buyCoins(player, 0));
    }
  }

  @Nested
  class PurchaseItemTests {

    @Test
    void purchaseItem_unknownItemId_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> shop.purchaseItem(player, "nonexistent-id", Difficulty.EASY));
    }

    @Test
    void purchaseItem_insufficientCoins_throwsInsufficientCoinsException() {
      assertThrows(InsufficientCoinsException.class,
          () -> shop.purchaseItem(player, "theme-midnight-black", Difficulty.EASY));
    }

    @Test
    void purchaseItem_validPurchase_playerOwnsTheme() throws InsufficientCoinsException {
      player.addCoins(15);
      shop.purchaseItem(player, "theme-midnight-black", Difficulty.EASY);
      assertTrue(player.ownsTheme("midnight-black"));
    }

    @Test
    void purchaseItem_validPurchase_coinsAreDeducted() throws InsufficientCoinsException {
      player.addCoins(20);
      shop.purchaseItem(player, "theme-midnight-black", Difficulty.EASY);
      assertEquals(5, player.getCoins());
    }

    @Test
    void purchaseItem_alreadyOwnedTheme_activatesWithoutChargingCoins()
        throws InsufficientCoinsException {
      player.addCoins(15);
      shop.purchaseItem(player, "theme-midnight-black", Difficulty.EASY);
      int coinsAfterFirstPurchase = player.getCoins();

      shop.purchaseItem(player, "theme-midnight-black", Difficulty.EASY);

      assertEquals(coinsAfterFirstPurchase, player.getCoins());
      assertEquals("midnight-black", player.getActiveTheme());
    }

    @Test
    void purchaseItem_netWorthBelowRequirement_throwsException() {
      player.addCoins(35);
      assertThrows(IllegalArgumentException.class,
          () -> shop.purchaseItem(player, "theme-forest-fund", Difficulty.EASY));
    }

    @Test
    void purchaseItem_nullPlayer_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> shop.purchaseItem(null, "theme-midnight-black", Difficulty.EASY));
    }

    @Test
    void purchaseItem_nullDifficulty_throwsException() {
      player.addCoins(15);
      assertThrows(IllegalArgumentException.class,
          () -> shop.purchaseItem(player, "theme-midnight-black", null));
    }
  }
}

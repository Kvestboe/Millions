package edu.ntnu.idatt2003.gruppe50.domain.shop;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CoinExchangeTest {

  private CoinExchange exchange;

  @BeforeEach
  void setUp() {
    exchange = new CoinExchange(BigDecimal.valueOf(10_000));
  }

  @Nested
  class ConstructorTests {

    @Test
    void constructor_nullStartingCapital_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> new CoinExchange(null));
    }

    @Test
    void constructor_zeroStartingCapital_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new CoinExchange(BigDecimal.ZERO));
    }

    @Test
    void constructor_initialPriceHistoryHasOneEntry() {
      assertEquals(1, exchange.getPriceHistory().size());
    }
  }

  @Nested
  class CalculateCostTests {

    @Test
    void calculateCost_oneCoins_equalsPricePerCoin() {
      BigDecimal expected = exchange.getCurrentPricePerCoin();
      assertEquals(0, exchange.calculateCost(1).compareTo(expected));
    }

    @Test
    void calculateCost_multipleCoins_equalsMultipliedPrice() {
      BigDecimal expected = exchange.getCurrentPricePerCoin().multiply(BigDecimal.valueOf(5));
      assertEquals(0, exchange.calculateCost(5).compareTo(expected));
    }

    @Test
    void calculateCost_zeroCoins_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.calculateCost(0));
    }

    @Test
    void calculateCost_negativeCoins_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.calculateCost(-1));
    }
  }

  @Nested
  class WeekAdvancedTests {

    @Test
    void advanceShop_appendsPriceToHistory() {
      exchange.advanceShop();
      assertEquals(2, exchange.getPriceHistory().size());
    }

    @Test
    void advanceShop_priceStaysPositive() {
      for (int i = 0; i < 50; i++) {
        exchange.advanceShop();
      }
      assertTrue(exchange.getCurrentPricePerCoin().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void advanceShop_priceRemainsWithinBounds() {
      // Run many cycles; price must stay within [0.5x, 2.5x] of base price
      BigDecimal base = exchange.getCurrentPricePerCoin();
      BigDecimal min = base.multiply(new BigDecimal("0.5"));
      BigDecimal max = base.multiply(new BigDecimal("2.5"));

      for (int i = 0; i < 200; i++) {
        exchange.advanceShop();
        BigDecimal price = exchange.getCurrentPricePerCoin();
        assertTrue(price.compareTo(min) >= 0,
            "Price " + price + " dropped below minimum " + min);
        assertTrue(price.compareTo(max) <= 0,
            "Price " + price + " exceeded maximum " + max);
      }
    }
  }

  @Test
  void largerStartingCapital_producesHigherInitialPrice() {
    CoinExchange expensive = new CoinExchange(BigDecimal.valueOf(100_000));
    assertTrue(expensive.getCurrentPricePerCoin()
        .compareTo(exchange.getCurrentPricePerCoin()) > 0);
  }
}

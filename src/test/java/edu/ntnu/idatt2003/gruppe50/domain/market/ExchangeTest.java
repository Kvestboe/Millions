package edu.ntnu.idatt2003.gruppe50.domain.market;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Sale;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitSellOrder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExchangeTest {
  private Stock aapl, kog, eqnr;
  private Exchange exchange;
  private Player player;

  @BeforeEach
  void setup() {
    aapl = new Stock("AAPL", "Apple", bd(100));
    kog = new Stock("KOG", "Kongsberg", bd(200));
    eqnr = new Stock("EQNR", "Equinor", bd(50));
    exchange = new Exchange("test", List.of(aapl, kog, eqnr), new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile());
    player = new Player("test", bd(1000));
  }

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Exchange(null, List.of(aapl), new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile()));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Exchange("", List.of(aapl), new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile()));
  }

  @Test
  void constructor_nullStocks_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> new Exchange("Test", null, new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile()));
  }

  @Test
  void constructor_emptyList_throwsException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Exchange("Test", new ArrayList<>(), new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile()));
  }

  @Test
  void getName_returnsName() {
    assertEquals("test", exchange.getName());
  }

  @Test
  void getWeek_returnsWeek() {
    assertEquals(1, exchange.getWeek());
  }

  @Test
  void hasStock_returnsTrue() {
    assertTrue(exchange.hasStock("AAPL"));
  }

  @Test
  void hasStock_returnsFalse() {
    assertFalse(exchange.hasStock("MSFT"));
  }

  @Test
  void hasStock_blankSymbol_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.hasStock(""));
  }

  @Test
  void hasStock_nullSymbol_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.hasStock(null));
  }

  @Test
  void getStock_returnsStock() {
    assertEquals(aapl, exchange.getStock("AAPL"));
  }

  @Test
  void getStock_wrongSymbol_throwsException() {
    assertThrows(NoSuchElementException.class, () -> exchange.getStock("MSFT"));
  }

  @Test
  void getStock_nullSymbol_throwsHasStockException() {
    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> exchange.getStock(null));
    assertEquals("Symbol cannot be null or blank", exception.getMessage());
  }

  @Test
  void findStocks_findsStock() {
    assertEquals(aapl, exchange.findStocks("A").getFirst());
  }

  @Test
  void findStock_noStockFound() {
    assertTrue(exchange.findStocks("C").isEmpty());
  }

  @Nested
  class TransactionsTests {

    @Test
    void buy_nullSymbol_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.buy(null, bd(2), player));
    }

    @Test
    void buy_nonExistingSymbol_throwsException() {
      assertThrows(
          NoSuchElementException.class, () -> exchange.buy("MSFT", new BigDecimal("1"), player));
    }

    @Test
    void buy_negativeQuantity_throwsException() {
      assertThrows(
          IllegalArgumentException.class, () -> exchange.buy("AAPL", new BigDecimal("-5"), player));
    }

    @Test
    void buy_nullPlayer_throwsException() {
      assertThrows(
          IllegalArgumentException.class, () -> exchange.buy("AAPL", new BigDecimal("1"), null));
    }

    @Test
    void buy_nullQuantity_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.buy("AAPL", null, player));
    }

    @Test
    void buy_validPurchase_returnsTransaction() {
      Transaction t = exchange.buy("AAPL", new BigDecimal("2"), player);
      assertNotNull(t);
      assertInstanceOf(Purchase.class, t);
    }

    @Test
    void sell_nullShare_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.sell(null, player));
    }

    @Test
    void sell_nullPlayer_throwsException() {
      Share share = new Share(exchange.getStock("AAPL"), new BigDecimal("1"), new BigDecimal("100"),
          1);
      assertThrows(IllegalArgumentException.class, () -> exchange.sell(share.getShareId(), null));
    }

    @Test
    void sell_validSale_returnsTransaction() {
      exchange.buy("AAPL", bd(1), player);
      Transaction t =
          exchange.sell(player.getPortfolio().getShares("AAPL").getFirst().getShareId(), player);
      assertNotNull(t);
      assertInstanceOf(Sale.class, t);
    }
  }

  @Test
  void advance_incrementsWeek() {
    exchange.advance();
    assertEquals(2, exchange.getWeek());
  }

  @Test
  void advance_updatesStockPrices() {
    Stock stock = exchange.getStock("AAPL");
    int historyBefore = stock.getHistoricalPrices().size();
    exchange.advance();
    assertEquals(historyBefore + 1, stock.getHistoricalPrices().size());
  }

  @Test
  void buy_zeroQuantity_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.buy("AAPL", bd(0), player));
  }

  @Test
  void findStocks_caseInsensitive() {
    assertFalse(exchange.findStocks("aapl").isEmpty());
    assertFalse(exchange.findStocks("AAPL").isEmpty());
  }

  @Nested
  class GetStockInDirectionTests {

    @Test
    void getGainers_negativeLimit_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(-1));
    }

    @Test
    void getGainers_zeroLimit_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(0));
    }

    @Test
    void getLosers_negativeLimit_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(-1));
    }

    @Test
    void getLosers_zeroLimit_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(0));
    }

    @Test
    void getGainers_returnsSortedByLatestPriceChange_descending() {
      aapl.addNewSalesPrice(bd(120));
      kog.addNewSalesPrice(bd(180));
      eqnr.addNewSalesPrice(bd(65));

      assertEquals(List.of(eqnr, aapl, kog), exchange.getGainers(3));
    }

    @Test
    void getGainers_returnsSortedByLatestPercentChange_descending() {
      aapl.addNewSalesPrice(bd(120));
      kog.addNewSalesPrice(bd(180));
      eqnr.addNewSalesPrice(bd(65));

      assertEquals(List.of(eqnr, aapl, kog), exchange.getGainers(3));
    }

    @Test
    void getLosers_returnsSortedByLatestPercentChange_ascending() {
      aapl.addNewSalesPrice(bd(120));
      kog.addNewSalesPrice(bd(180));
      eqnr.addNewSalesPrice(bd(65));

      assertEquals(List.of(kog, aapl, eqnr), exchange.getLosers(3));
    }

    @Test
    void getGainers_respectsLimit() {
      aapl.addNewSalesPrice(bd(120));
      kog.addNewSalesPrice(bd(180));
      eqnr.addNewSalesPrice(bd(65));

      assertEquals(List.of(eqnr, aapl), exchange.getGainers(2));
    }

    @Test
    void getLosers_respectsLimit() {
      aapl.addNewSalesPrice(bd(120));
      kog.addNewSalesPrice(bd(180));
      eqnr.addNewSalesPrice(bd(65));

      assertEquals(List.of(kog, aapl), exchange.getLosers(2));
    }
  }


  @Nested
  class PendingOrderProcessingTests {

    @Test
    void advance_triggeredBuyOrder_isRemovedFromPending() {
      LimitBuyOrder order = new LimitBuyOrder(aapl, player, bd(999999), bd(1), 1);
      exchange.placeOrder(order);

      exchange.advance();

      assertTrue(exchange.getPendingOrders().isEmpty());
    }

    @Test
    void advance_triggeredBuyOrder_playerReceivesShares() {
      LimitBuyOrder order = new LimitBuyOrder(aapl, player, bd(999999), bd(1), 1);
      exchange.placeOrder(order);

      exchange.advance();

      assertFalse(player.getPortfolio().getShares("AAPL").isEmpty());
    }

    @Test
    void advance_triggeredSellOrder_isRemovedFromPending() {
      exchange.buy("AAPL", bd(1), player);
      LimitSellOrder order = new LimitSellOrder(aapl, player, bd(1), bd(1), 1);
      exchange.placeOrder(order);

      exchange.advance();

      assertTrue(exchange.getPendingOrders().isEmpty());
    }

    @Test
    void advance_expiredOrder_isRemovedWithoutExecuting() {
      LimitBuyOrder order = new LimitBuyOrder(aapl, player, bd(1), bd(1), 1, 2);
      exchange.placeOrder(order);

      exchange.advance();
      exchange.advance();

      assertTrue(exchange.getPendingOrders().isEmpty());
      assertTrue(player.getPortfolio().getShares("AAPL").isEmpty());
    }

    @Test
    void advance_failedOrderExecution_isStillRemovedFromPending() {
      LimitBuyOrder order = new LimitBuyOrder(aapl, player, bd(999999), bd(100000), 1);
      exchange.placeOrder(order);

      assertDoesNotThrow(() -> exchange.advance());
      assertTrue(exchange.getPendingOrders().isEmpty());
    }
  }

  @Nested
  class SellQuantityTests {

    @Test
    void sellQuantity_exactSingleLot_removesShareAndCreditsPlayer() {
      exchange.buy("AAPL", bd(3), player);
      BigDecimal moneyAfterBuy = player.getMoney();

      exchange.sellQuantity(aapl, bd(3), player);

      assertTrue(player.getPortfolio().getShares("AAPL").isEmpty());
      assertTrue(player.getMoney().compareTo(moneyAfterBuy) > 0);
    }

    @Test
    void sellQuantity_partialLot_splitsLotAndLeavesRemainder() {
      exchange.buy("AAPL", bd(5), player);

      exchange.sellQuantity(aapl, bd(2), player);

      List<Share> remaining = player.getPortfolio().getShares("AAPL");
      BigDecimal totalRemaining = remaining.stream()
          .map(Share::getQuantity)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      assertEquals(0, totalRemaining.compareTo(bd(3)));
    }

    @Test
    void sellQuantity_acrossMultipleLots_usedFifoOrder() {
      exchange.buy("AAPL", bd(2), player);
      aapl.addNewSalesPrice(bd(150));
      exchange.buy("AAPL", bd(3), player);

      exchange.sellQuantity(aapl, bd(2), player);

      List<Share> remaining = player.getPortfolio().getShares("AAPL");
      BigDecimal totalRemaining = remaining.stream()
          .map(Share::getQuantity)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      assertEquals(0, totalRemaining.compareTo(bd(3)));
    }

    @Test
    void sellQuantity_moreThanOwned_throwsException() {
      exchange.buy("AAPL", bd(2), player);

      assertThrows(IllegalStateException.class,
          () -> exchange.sellQuantity(aapl, bd(5), player));
    }

    @Test
    void sellQuantity_nullStock_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> exchange.sellQuantity(null, bd(1), player));
    }

    @Test
    void sellQuantity_nullPlayer_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> exchange.sellQuantity(aapl, bd(1), null));
    }

    @Test
    void sellQuantity_zeroQuantity_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> exchange.sellQuantity(aapl, bd(0), player));
    }
  }
}

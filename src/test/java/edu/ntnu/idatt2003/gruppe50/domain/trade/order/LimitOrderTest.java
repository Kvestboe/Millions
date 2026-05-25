package edu.ntnu.idatt2003.gruppe50.domain.trade.order;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createAAPLStock;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultPlayer;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.LimitOrderDto;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LimitOrderTest {

  private Stock stock;
  private Player player;

  @BeforeEach
  void setUp() {
    stock = createAAPLStock();
    player = createDefaultPlayer();
  }

  @Nested
  class LimitBuyOrderTests {

    @Test
    void shouldTrigger_priceAtTarget_returnsTrue() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(100)));
    }

    @Test
    void shouldTrigger_priceBelowTarget_returnsTrue() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(90)));
    }

    @Test
    void shouldTrigger_priceAboveTarget_returnsFalse() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1);
      assertFalse(order.shouldTrigger(bd(110)));
    }

    @Test
    void defaultDuration_setsExpiryToCurrentPlusDefault() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1);
      assertEquals(1 + LimitOrder.DEFAULT_DURATION_WEEKS, order.getExpiryWeek());
    }

    @Test
    void explicitExpiry_setsExpiryCorrectly() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1, 5);
      assertEquals(5, order.getExpiryWeek());
    }
  }

  @Nested
  class LimitSellOrderTests {

    @Test
    void shouldTrigger_priceAtTarget_returnsTrue() {
      LimitSellOrder order = new LimitSellOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(100)));
    }

    @Test
    void shouldTrigger_priceAboveTarget_returnsTrue() {
      LimitSellOrder order = new LimitSellOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(110)));
    }

    @Test
    void shouldTrigger_priceBelowTarget_returnsFalse() {
      LimitSellOrder order = new LimitSellOrder(stock, player, bd(100), bd(1), 1);
      assertFalse(order.shouldTrigger(bd(90)));
    }
  }

  @Nested
  class StopLossOrderTests {

    @Test
    void shouldTrigger_priceAtTarget_returnsTrue() {
      StopLossOrder order = new StopLossOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(100)));
    }

    @Test
    void shouldTrigger_priceBelowTarget_returnsTrue() {
      StopLossOrder order = new StopLossOrder(stock, player, bd(100), bd(1), 1);
      assertTrue(order.shouldTrigger(bd(90)));
    }

    @Test
    void shouldTrigger_priceAboveTarget_returnsFalse() {
      StopLossOrder order = new StopLossOrder(stock, player, bd(100), bd(1), 1);
      assertFalse(order.shouldTrigger(bd(110)));
    }
  }

  @Nested
  class IsExpiredTests {

    @Test
    void isExpired_weekAfterExpiry_returnsTrue() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1, 3);
      assertTrue(order.isExpired(4));
    }

    @Test
    void isExpired_weekAtExpiry_returnsFalse() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1, 3);
      assertFalse(order.isExpired(3));
    }

    @Test
    void isExpired_weekBeforeExpiry_returnsFalse() {
      LimitBuyOrder order = new LimitBuyOrder(stock, player, bd(100), bd(1), 1, 3);
      assertFalse(order.isExpired(2));
    }
  }

  @Nested
  class ConstructorValidationTests {

    @Test
    void nullStock_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(null, player, bd(100), bd(1), 1));
    }

    @Test
    void nullPlayer_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, null, bd(100), bd(1), 1));
    }

    @Test
    void nullTargetPrice_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, null, bd(1), 1));
    }

    @Test
    void zeroTargetPrice_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, bd(0), bd(1), 1));
    }

    @Test
    void zeroQuantity_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, bd(100), bd(0), 1));
    }

    @Test
    void expiryEqualToCurrentWeek_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, bd(100), bd(1), 5, 5));
    }

    @Test
    void expiryBeforeCurrentWeek_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, bd(100), bd(1), 5, 4));
    }

    @Test
    void durationExceedsMax_throwsException() {
      int tooFarExpiry = 1 + LimitOrder.MAX_DURATION_WEEKS + 1;
      assertThrows(IllegalArgumentException.class,
          () -> new LimitBuyOrder(stock, player, bd(100), bd(1), 1, tooFarExpiry));
    }

    @Test
    void durationAtMax_doesNotThrow() {
      int maxExpiry = 1 + LimitOrder.MAX_DURATION_WEEKS;
      assertDoesNotThrow(() -> new LimitBuyOrder(stock, player, bd(100), bd(1), 1, maxExpiry));
    }
  }

  @Nested
  class BuildOrderTests {

    private final Map<String, Stock> stockMap = Map.of("AAPL", createAAPLStock());

    @Test
    void buildOrder_buyType_returnsLimitBuyOrder() {
      LimitOrderDto dto = new LimitOrderDto("BUY", "AAPL", bd(100), bd(1), 1, 4);
      assertInstanceOf(LimitBuyOrder.class, LimitOrder.buildOrder(dto, stockMap, player));
    }

    @Test
    void buildOrder_sellType_returnsLimitSellOrder() {
      LimitOrderDto dto = new LimitOrderDto("SELL", "AAPL", bd(100), bd(1), 1, 4);
      assertInstanceOf(LimitSellOrder.class, LimitOrder.buildOrder(dto, stockMap, player));
    }

    @Test
    void buildOrder_stopLossType_returnsStopLossOrder() {
      LimitOrderDto dto = new LimitOrderDto("STOP_LOSS", "AAPL", bd(100), bd(1), 1, 4);
      assertInstanceOf(StopLossOrder.class, LimitOrder.buildOrder(dto, stockMap, player));
    }

    @Test
    void buildOrder_unknownType_throwsException() {
      LimitOrderDto dto = new LimitOrderDto("UNKNOWN", "AAPL", bd(100), bd(1), 1, 4);
      assertThrows(IllegalArgumentException.class,
          () -> LimitOrder.buildOrder(dto, stockMap, player));
    }
  }
}

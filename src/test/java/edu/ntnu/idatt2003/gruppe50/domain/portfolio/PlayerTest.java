package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import static edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status.INVESTOR;
import static edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status.NOVICE;
import static edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status.SPECULATOR;
import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PlayerTest {
  private Player player;

  @BeforeEach
  void setup() {
    player = new Player("test", bd(100));
  }

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player(null, bd(100)));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player(" ", bd(100)));
  }

  @Test
  void constructor_negativeMoney_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player("test", bd(-100)));
  }

  @Test
  void constructor_nullMoney_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player("test", null));
  }

  @Test
  void getName_returnsPlayerName() {
    assertEquals("test", player.getName());
  }

  @Test
  void getMoney_returnsCurrentBalance() {
    assertEquals(bd(100), player.getMoney());
  }

  @Test
  void addMoney_increasesBalanceByGivenAmount() {
    player.addMoney(bd(10));
    assertEquals(0, bd(110).compareTo(player.getMoney()));
  }

  @Test
  void addMoney_nullAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(null));
  }

  @Test
  void addMoney_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(bd(-10)));
  }

  @Test
  void addMoney_zeroAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(bd(0)));
  }

  @Test
  void withdrawMoney_decreasesBalanceByGivenAmount() {
    player.withdrawMoney(bd(10));
    assertEquals(0, bd(90).compareTo(player.getMoney()));
  }

  @Test
  void withdrawMoney_nullAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(null));
  }

  @Test
  void withdrawMoney_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(bd(-10)));
  }

  @Test
  void withdrawMoney_zeroAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(bd(0)));
  }

  @Test
  void getPortfolio_returnsPortfolio() {
    assertInstanceOf(Portfolio.class, player.getPortfolio());
  }

  @Test
  void getTransactionArchive_returnsTransactionArchive() {
    assertInstanceOf(TransactionArchive.class, player.getTransactionArchive());
  }

  @Test
  void getNetWorth_returnsCashAndPortfolioValue() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd(100));
    Share share = new Share(stock, bd(5), bd(100), 1);
    player.getPortfolio().addShare(share);

    BigDecimal expectedPrice = bd(595);
    assertEquals(expectedPrice, player.getNetWorth().stripTrailingZeros());
  }

  @Test
  void getStatus_beforeWeek10_returnsNovice() {
    tradeForWeeks(1);
    player.addMoney(bd(1000));
    assertEquals(NOVICE, player.getStatus());
  }

  @Test
  void getStatus_week10_over20Percent_returnsInvestor() {
    tradeForWeeks(10);
    player.addMoney(bd(21));
    assertEquals(INVESTOR, player.getStatus());
  }

  @Test
  void getStatus_week10_under20Percent_returnsNotInvestor() {
    tradeForWeeks(10);
    player.addMoney(bd(1));
    assertEquals(NOVICE, player.getStatus());
    assertNotEquals(INVESTOR, player.getStatus());
  }

  @Test
  void getStatus_week20_over100Percent_returnsSpeculator() {
    tradeForWeeks(20);
    player.addMoney(bd(101));
    assertEquals(SPECULATOR, player.getStatus());
  }

  @Test
  void getStatus_week20_under100Percent_returnsNotSpeculator() {
    tradeForWeeks(20);
    player.addMoney(bd(50));
    assertNotEquals(SPECULATOR, player.getStatus());
    assertEquals(INVESTOR, player.getStatus());
  }

  // helper: registers one transaction per week so countDistinctWeeks() == weeks
  private void tradeForWeeks(int weeks) {
    Stock stock = new Stock("AAPL", "Apple", bd(100));
    for (int week = 1; week <= weeks; week++) {
      Share share = new Share(stock, bd(1), bd(100), week);
      player.getTransactionArchive().add(new Purchase(share, week, UUID.randomUUID()));
    }
  }

  @Nested
  class StatusTests {

    @Test
    void displayName_novice_returnsCapitalized() {
      assertEquals("Novice", Status.NOVICE.displayName());
    }

    @Test
    void displayName_investor_returnsCapitalized() {
      assertEquals("Investor", Status.INVESTOR.displayName());
    }

    @Test
    void displayName_speculator_returnsCapitalized() {
      assertEquals("Speculator", Status.SPECULATOR.displayName());
    }

    @Test
    void next_novice_returnsInvestor() {
      assertEquals(Status.INVESTOR, Status.NOVICE.next());
    }

    @Test
    void next_investor_returnsSpeculator() {
      assertEquals(Status.SPECULATOR, Status.INVESTOR.next());
    }

    @Test
    void next_speculator_returnsNull() {
      assertNull(Status.SPECULATOR.next());
    }

    @Test
    void getRequiredWeeks_novice_returnsZero() {
      assertEquals(0, Status.NOVICE.getRequiredWeeks());
    }

    @Test
    void getRequiredGain_novice_returnsZero() {
      assertEquals(0, BigDecimal.ZERO.compareTo(Status.NOVICE.getRequiredGain()));
    }
  }
}

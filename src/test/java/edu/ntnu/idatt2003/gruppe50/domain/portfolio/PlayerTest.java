package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {
  private Player player;

  @BeforeEach
  void setup() {
    player = new Player("test", bd("100"));
  }

  @Test
  void constructor_nullName_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player(null, bd("100")));
  }

  @Test
  void constructor_blankName_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player(" ", bd("100")));
  }

  @Test
  void constructor_negativeMoney_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Player("test", bd("-100")));
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
    assertEquals(bd("100"), player.getMoney());
  }

  @Test
  void addMoney_increasesBalanceByGivenAmount() {
    player.addMoney(bd("10"));
    assertEquals(0, bd("110").compareTo(player.getMoney()));
  }

  @Test
  void addMoney_nullAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(null));
  }

  @Test
  void addMoney_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(bd("-10")));
  }

  @Test
  void addMoney_zeroAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.addMoney(bd("0")));
  }

  @Test
  void withdrawMoney_decreasesBalanceByGivenAmount() {
    player.withdrawMoney(bd("10"));
    assertEquals(0, bd("90").compareTo(player.getMoney()));
  }

  @Test
  void withdrawMoney_nullAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(null));
  }

  @Test
  void withdrawMoney_negativeAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(bd("-10")));
  }

  @Test
  void withdrawMoney_zeroAmount_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.withdrawMoney(bd("0")));
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
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("100"));
    Share share = new Share(stock, bd("5"), bd("100"), 1);
    player.getPortfolio().addShare(share);

    BigDecimal expectedPrice = bd("595"); // money(100) + portfolioNetWorth(495)
    assertEquals(expectedPrice, player.getNetWorth().stripTrailingZeros());
  }

  @Test
  void getStatus_nullExchange_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> player.getStatus(null));
  }

  @Test
  void getStatus_beforeWeek10_returnsNovice() {
    Exchange ex = exchangeAtWeek(1);
    player.addMoney(bd(1000));
    assertEquals("Novice", player.getStatus(ex));
  }

  @Test
  void getStatus_week10_over20Percent_returnsInvestor() {
    Exchange ex = exchangeAtWeek(10);
    player.addMoney(bd(21));
    assertEquals("Investor", player.getStatus(ex));
  }

  @Test
  void getStatus_week10_under20Percent_returnsNotInvestor() {
    Exchange ex = exchangeAtWeek(10);
    player.addMoney(bd(20));
    assertEquals("Novice", player.getStatus(ex));
    assertNotEquals("Investor", player.getStatus(ex));
  }

  @Test
  void getStatus_week20_over100Percent_returnsSpeculator() {
    Exchange ex = exchangeAtWeek(20);
    player.addMoney(bd(101));
    assertEquals("Speculator", player.getStatus(ex));
  }

  @Test
  void getStatus_week20_under100Percent_returnsNotSpeculator() {
    Exchange ex = exchangeAtWeek(20);
    player.addMoney(bd(100));
    assertNotEquals("Speculator", player.getStatus(ex));
    assertEquals("Investor", player.getStatus(ex));
  }

  // helper method
  private Exchange exchangeAtWeek(int week) {
    Exchange ex =
        new Exchange("OSL", List.of(new Stock("AAPL", "Apple", bd(100))), new TransactionFactory());
    for (int i = 0; i < week; i++) {
      ex.advance();
    }
    return ex;
  }

  private BigDecimal bd(double num) {
    return BigDecimal.valueOf(num);
  }

  // Helper method
  private BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

package edu.ntnu.idatt2003.gruppe50.domain.trade;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SaleTest {
  private Stock stock;
  private Share share;
  private Sale sale;
  private Player player;
  private Purchase purchase;

  @BeforeEach
  void setup() {
    stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    share = new Share(stock, new BigDecimal("5"), bd("310"));
    sale = new Sale(share, 12);
    player = new Player("Test", bd("2000"));
    purchase = new Purchase(share, 1);
  }

  @Test
  void constructor_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Sale(null, 12));
  }

  @Test
  void constructor_negativeWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Sale(share, -12));
  }

  @Test
  void sale_isCommitted_returnsFalseInitially() {
    assertFalse(sale.isCommitted());
  }

  @Test
  void isCommitted_returnsTrue_afterSale_isComplete() {
    purchase.commit(player);
    sale.commit(player);
    assertTrue(sale.isCommitted());
  }

  @Test
  void constructor_validArguments_createsShare() {
    Stock stock2 = new Stock("AAPL", "Apple", bd("265"));
    Share share2 = new Share(stock, bd("3"), bd("250"));
    Sale sale2 = new Sale(share2, 12);

    assertEquals(share2, sale2.getShare());
    assertEquals(12, sale2.getWeek());
    assertInstanceOf(SaleCalculator.class, sale2.getCalculator());
  }

  @Test
  void commit_nullPlayer_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> sale.commit(null));
  }

  @Test
  void commit_throwsException_whenPlayerDoesNotOwnShare() {
    assertThrows(IllegalArgumentException.class, () -> sale.commit(player));

    assertFalse(sale.isCommitted());
  }

  @Test
  void commit_doesNothing_whenAlreadyCommitted() {
    purchase.commit(player);
    sale.commit(player);
    assertTrue(sale.isCommitted());

    BigDecimal moneyBefore = player.getMoney();
    int shareBefore = player.getPortfolio().getShares().size();
    int transactionArchiveBefore = player.getTransactionArchive().getTransactions(12).size();

    sale.commit(player);

    assertEquals(moneyBefore, player.getMoney());
    assertEquals(shareBefore, player.getPortfolio().getShares().size());
    assertEquals(
        transactionArchiveBefore, player.getTransactionArchive().getTransactions(12).size());
  }

  @Test
  void commit_updatesPlayerState_whenSuccessful() {
    purchase.commit(player);

    BigDecimal moneyBefore = player.getMoney();
    int shareBefore = player.getPortfolio().getShares().size();
    int transactionArchiveBefore = player.getTransactionArchive().getTransactions(12).size();

    sale.commit(player);

    BigDecimal moneyAfter = moneyBefore.add(sale.getCalculator().calculateTotal());

    assertEquals(moneyAfter, player.getMoney());
    assertEquals(shareBefore - 1, player.getPortfolio().getShares().size());
    assertEquals(
        transactionArchiveBefore + 1, player.getTransactionArchive().getTransactions(12).size());
    assertTrue(sale.isCommitted());
  }

  // Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

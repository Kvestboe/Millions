package edu.ntnu.idatt2003.gruppe50.domain.trade;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PurchaseTest {
  private Share share;
  private Purchase purchase;
  private Player richPlayer;
  private Player poorPlayer;

  @BeforeEach
  void setup() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    share = new Share(stock, new BigDecimal("5"), bd("310"), 1);
    purchase = new Purchase(share, 12, UUID.randomUUID());
    richPlayer = new Player("Test", bd("20000"));
    poorPlayer = new Player("Test2", bd("1"));
  }

  @Test
  void constructor_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Purchase(null, 12, UUID.randomUUID()));
  }

  @Test
  void constructor_negativeWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new Purchase(share, -12, UUID.randomUUID()));
  }

  @Test
  void purchase_isCommitted_returnsFalseInitially() {
    assertFalse(purchase.isCommitted());
  }

  @Test
  void isCommitted_returnsTrue_afterPurchase_isComplete() {
    purchase.commit(richPlayer);
    assertTrue(purchase.isCommitted());
  }

  @Test
  void constructor_validArguments_createsPurchase() {
    Stock stock2 = new Stock("AAPL", "Apple", new BigDecimal("265"));
    Share share2 = new Share(stock2, new BigDecimal("3"), new BigDecimal("250"), 1);
    Purchase purchase2 = new Purchase(share2, 12, UUID.randomUUID());

    assertEquals(share2, purchase2.getShare());
    assertEquals(12, purchase2.getWeek());
    assertInstanceOf(PurchaseCalculator.class, purchase2.getCalculator());
  }

  @Test
  void commit_nullPlayer_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> purchase.commit(null));
  }

  @Test
  void isCommitted_remainsFalse_whenCommitFails_dueToInsufficientFunds() {
    assertThrows(IllegalArgumentException.class, () -> purchase.commit(poorPlayer));
    assertFalse(purchase.isCommitted());
  }

  @Test
  void commit_doesNothing_whenAlreadyCommitted() {
    purchase.commit(richPlayer);
    BigDecimal moneyBefore = richPlayer.getMoney();
    int shareBefore = richPlayer.getPortfolio().getShares().size();
    int transactionArchiveBefore = richPlayer.getTransactionArchive().getTransactions(12).size();

    purchase.commit(richPlayer);

    assertEquals(moneyBefore, richPlayer.getMoney());
    assertEquals(shareBefore, richPlayer.getPortfolio().getShares().size());
    assertEquals(
        transactionArchiveBefore, richPlayer.getTransactionArchive().getTransactions(12).size());
  }

  @Test
  void commit_updatesPlayerState_whenSuccessful() {
    BigDecimal moneyBefore = richPlayer.getMoney();
    int shareBefore = richPlayer.getPortfolio().getShares().size();
    int transactionArchiveBefore = richPlayer.getTransactionArchive().getTransactions(12).size();

    purchase.commit(richPlayer);

    BigDecimal moneyAfter = moneyBefore.subtract(purchase.getCalculator().calculateTotal());

    assertEquals(moneyAfter, richPlayer.getMoney());
    assertEquals(shareBefore + 1, richPlayer.getPortfolio().getShares().size());
    assertEquals(
        transactionArchiveBefore + 1,
        richPlayer.getTransactionArchive().getTransactions(12).size());
  }

  // Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

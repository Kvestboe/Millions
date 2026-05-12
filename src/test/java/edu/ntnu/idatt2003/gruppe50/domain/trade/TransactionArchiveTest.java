package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionArchiveTest {
  private Share share;
  private TransactionArchive archive;

  @BeforeEach
  void setup() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    share = new Share(stock, bd("5"), bd("310"));
    archive = new TransactionArchive();
  }

  @Test
  void newTransactionArchive_isEmpty() {
    assertTrue(new TransactionArchive().isEmpty());
  }

  @Test
  void addingNullTransaction_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> archive.add(null));
  }

  @Test
  void getTransaction_returnsFalseIfAlreadyContains() {
    Purchase purchase = new Purchase(share, 1);
    assertTrue(archive.add(purchase));
    assertFalse(archive.add(purchase));
    assertTrue(archive.add(new Purchase(share, 1)));
  }

  @Test
  void getTransaction_returnsTransaction() {
    Purchase purchase = new Purchase(share, 1);
    archive.add(purchase);
    assertSame(purchase, archive.getTransactions(1).getFirst());
  }

  @Test
  void getPurchase_returnsPurchase() {
    Purchase purchase = new Purchase(share, 1);
    archive.add(purchase);
    assertSame(purchase, archive.getPurchases(1).getFirst());
  }

  @Test
  void getSales_returnsSale() {
    Sale sale = new Sale(share, 1);
    archive.add(sale);
    assertSame(sale, archive.getSales(1).getFirst());
  }

  @Test
  void countDistinctWeeks_countsRight() {
    archive.add(new Purchase(share, 1));
    assertEquals(1, archive.countDistinctWeeks());
  }

  //Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

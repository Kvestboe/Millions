package edu.ntnu.idatt2003.gruppe50.domain.trade;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionArchiveTest {
  private Share share;
  private TransactionArchive archive;

  @BeforeEach
  void setup() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    share = new Share(stock, bd("5"), bd("310"), 1);
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
  void constructor_validArguments_createsShare() {
    Purchase purchase = new Purchase(share, 1, UUID.randomUUID());
    assertTrue(archive.add(purchase));
    assertFalse(archive.add(purchase));
    assertTrue(archive.add(new Purchase(share, 1, UUID.randomUUID())));
  }

  @Test
  void getTransaction_returnsTransaction() {
    Purchase purchase = new Purchase(share, 1, UUID.randomUUID());
    archive.add(purchase);
    assertSame(purchase, archive.getTransactions(1).getFirst());
  }

  @Test
  void getPurchase_returnsPurchase() {
    Purchase purchase = new Purchase(share, 1, UUID.randomUUID());
    archive.add(purchase);
    assertSame(purchase, archive.getPurchases(1).getFirst());
  }

  @Test
  void getSales_returnsSale() {
    Sale sale = new Sale(share, 1, UUID.randomUUID());
    archive.add(sale);
    assertSame(sale, archive.getSales(1).getFirst());
  }

  @Test
  void countDistinctWeeks_countsRight() {
    archive.add(new Purchase(share, 1, UUID.randomUUID()));
    assertEquals(1, archive.countDistinctWeeks());
  }

  // Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

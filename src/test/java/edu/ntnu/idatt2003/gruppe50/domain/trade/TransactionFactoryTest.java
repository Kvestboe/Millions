package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFactoryTest {
  private TransactionFactory factory;
  private Share share;

  @BeforeEach
  void setup() {
    factory = new TransactionFactory();
    Stock stock = new Stock("AAPL", "Apple", bd("250"));
    share = new Share(stock, bd("3"), bd("250"));
  }

  @Test
  void createPurchase_validInput_returnsPurchase() {
    Transaction t = factory.createPurchase(share, 1);

    assertInstanceOf(Purchase.class, t);
    assertEquals(share, t.getShare());
    assertEquals(1, t.getWeek());
  }

  @Test
  void createPurchase_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createPurchase(null, 1));
  }

  @Test
  void createPurchase_negativeWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createPurchase(share, -1));
  }

  @Test
  void createPurchase_zeroWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createPurchase(share, 0));
  }

  @Test
  void createSale_validInput_returnsSale() {
    Transaction t = factory.createSale(share, 1);

    assertInstanceOf(Sale.class, t);
    assertEquals(share, t.getShare());
    assertEquals(1, t.getWeek());
  }

  @Test
  void createSale_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createSale(null, 1));
  }

  @Test
  void createSale_negativeWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createSale(share, -1));
  }

  @Test
  void createSale_zeroWeek_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> factory.createSale(share, 0));
  }

  //Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
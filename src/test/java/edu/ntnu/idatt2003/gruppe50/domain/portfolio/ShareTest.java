package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShareTest {
  private Stock stock;
  private Share share;

  @BeforeEach
  void setUp() {
    stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    share = new Share(stock, bd("5"), bd("310"));
  }

  @Test
  void constructor_validArguments_createsShare() {
    Stock stock = new Stock("AAPL", "Apple", bd("265"));
    Share share = new Share(stock, bd("3"), bd("250"));

    assertEquals(stock, share.getStock());
    assertEquals(bd("3"), share.getQuantity());
    assertEquals(bd("250"), share.getPurchasePrice());
  }

  @Test
  void constructor_nullStock_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(null, bd("10"), bd("6")));
  }

  @Test
  void constructor_negativeQuantity_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(stock, bd("-7"), bd("20")));
  }

  @Test
  void constructor_nullQuantity_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(stock, null, bd("20")));
  }

  @Test
  void constructor_zeroQuantity_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(stock, BigDecimal.ZERO, bd("20")));
  }

  @Test
  void constructor_negativePurchasePrice_throwsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> new Share(stock, bd("7"), new BigDecimal("-1")));
  }

  @Test
  void constructor_nullPurchasePrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(stock, bd("7"), null));
  }

  @Test
  void constructor_zeroPurchasePrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Share(stock, bd("7"), BigDecimal.ZERO));
  }

  @Test
  void getStock_returnsStock() {
    assertEquals(stock, share.getStock());
  }

  @Test
  void getQuantity_returnsQuantity() {
    assertEquals(bd("5"), share.getQuantity());
  }

  @Test
  void getPurchasePrice_returnsPurchasePrice() {
    assertEquals(bd("310"), share.getPurchasePrice());
  }

  // Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

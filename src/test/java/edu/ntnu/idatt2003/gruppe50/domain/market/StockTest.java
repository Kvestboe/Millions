package edu.ntnu.idatt2003.gruppe50.domain.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockTest {
  private Stock stock;

  @BeforeEach
  void setUp() {
    stock = new Stock("KOG", "Kongsberg Gruppen", bd(330));
  }

  @Test
  void constructor_validArguments_createsStock() {
    Stock stock = new Stock("AAPL", "Apple", bd(265));

    assertEquals("AAPL", stock.getSymbol());
    assertEquals("Apple", stock.getCompany());
    assertEquals(bd(265), stock.getSalesPrice());
  }

  @Test
  void constructor_nullSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock(null, "Apple", bd(265)));
  }

  @Test
  void constructor_blankSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("   ", "Apple", bd(265)));
  }

  @Test
  void constructor_nullCompany_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", null, bd(265)));
  }

  @Test
  void constructor_blankCompany_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", "   ", bd(265)));
  }

  @Test
  void constructor_nullSalesPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", "Apple", null));
  }

  @Test
  void constructor_negativeSalesPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", "Apple", bd(-265)));
  }

  @Test
  void constructor_zeroSalesPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", "Apple", bd(0)));
  }

  @Test
  void getSymbol_returnsSymbol() {
    assertEquals("KOG", stock.getSymbol());
  }

  @Test
  void getCompany_returnsCompany() {
    assertEquals("Kongsberg Gruppen", stock.getCompany());
  }

  @Test
  void getSalesPrice_returnsLatestSalesPrice() {
    assertEquals(bd(330), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPrice_updatesLatestSalesPrice() {
    stock.addNewSalesPrice(bd(340));
    assertEquals(bd(340), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPrice_multipleUpdates_returnsMostRecent() {
    stock.addNewSalesPrice(bd(340));
    stock.addNewSalesPrice(bd(350));
    assertEquals(bd(350), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPrice_null_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> stock.addNewSalesPrice(null));
  }

  @Test
  void addNewSalesPrice_zero_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> stock.addNewSalesPrice(BigDecimal.ZERO));
  }

  @Test
  void addNewSalesPrice_negative_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> stock.addNewSalesPrice(bd(-1)));
  }

  @Test
  void getHighestPrice_returnsHighest() {
    stock.addNewSalesPrice(bd(340));
    stock.addNewSalesPrice(bd(320));
    assertEquals(bd(340), stock.getHighestPrice());
  }

  @Test
  void getLowestPrice_returnsLowest() {
    stock.addNewSalesPrice(bd(100));
    stock.addNewSalesPrice(bd(200));
    assertEquals(bd(100), stock.getLowestPrice());
  }

  @Test
  void getLatestPriceChange_priceIncrease_positiveChangeReturned() {
    stock.addNewSalesPrice(bd(100));
    stock.addNewSalesPrice(bd(120));
    assertEquals(bd(20), stock.getLatestPriceChange());
  }

  @Test
  void getLatestPriceChange_priceDecrease_negativeChangeReturned() {
    stock.addNewSalesPrice(bd(120));
    stock.addNewSalesPrice(bd(100));
    assertEquals(bd(-20), stock.getLatestPriceChange());
  }

  @Test
  void getHistoricalPrices_returnsImmutableList() {
    List<BigDecimal> prices = stock.getHistoricalPrices();
    assertThrows(UnsupportedOperationException.class, () -> prices.add(bd(100)));
  }

  @Test
  void addPrice_updatesHistory() {
    assertEquals(1, stock.getHistoricalPrices().size());
    stock.addNewSalesPrice(bd(100));
    assertEquals(2, stock.getHistoricalPrices().size());
  }

  // Helper method
  private static BigDecimal bd(double value) {
    return new BigDecimal(value);
  }
}

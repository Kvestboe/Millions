package edu.ntnu.idatt2003.gruppe50.domain.trade.calculator;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SaleCalculatorTest {
  private Share share;
  private SaleCalculator calc;

  @BeforeEach
  void setup() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("100"));
    share = new Share(stock, bd("5"), bd("100"));
    calc = new SaleCalculator(share);
  }

  @Test
  void constructor_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new SaleCalculator(null));
  }

  @Test
  void constructor_validArgument_createsCalculator() {
    assertDoesNotThrow(() -> new SaleCalculator(share));
  }

  @Test
  void calculateGross_returnsCorrectValue() {
    assertEquals(bd("500"), calc.calculateGross());
  }

  @Test
  void calculateCommission_returnsFivePercentOfGross() {
    assertEquals(bd("5"), calc.calculateCommission().stripTrailingZeros());
  }

  @Test
  void calculateTax_returnsZero() {
    assertEquals(BigDecimal.ZERO, calc.calculateTax().stripTrailingZeros());
  }

  @Test
  void calculateTax_profitableSale_tax30Percent() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("200"));
    share = new Share(stock, bd("1"), bd("100"));
    calc = new SaleCalculator(share);
    BigDecimal profit = bd("100").subtract(calc.calculateCommission());

    assertEquals(profit.multiply(bd("0.3")), calc.calculateTax());
  }

  @Test
  void calculateTotal_returnsCorrectTotal() {
    assertEquals(bd("495"), calc.calculateTotal().stripTrailingZeros());
  }

  // Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

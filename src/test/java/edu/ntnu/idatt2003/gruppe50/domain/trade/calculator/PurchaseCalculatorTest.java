package edu.ntnu.idatt2003.gruppe50.domain.trade.calculator;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseCalculatorTest {
  private Share share;
  private PurchaseCalculator calc;

  @BeforeEach
  void setup() {
    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("100"));
    share = new Share(stock, bd("5"), bd("100"));
    calc = new PurchaseCalculator(share);
  }

  @Test
  void constructor_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> new PurchaseCalculator(null));
  }

  @Test
  void constructor_validArgument_createsCalculator() {
    assertDoesNotThrow(() -> new PurchaseCalculator(share));
  }

  @Test
  void calculateGross_returnsCorrectValue() {
    assertEquals(bd("500"), calc.calculateGross());
  }

  @Test
  void calculateCommission_returnsFivePercentOfGross() {
    assertEquals(new BigDecimal("2.5"), calc.calculateCommission().stripTrailingZeros());
  }

  @Test
  void calculateTax_returnsZero() {
    assertEquals(BigDecimal.ZERO, calc.calculateTax().stripTrailingZeros());
  }

  @Test
  void calculateTotal_returnsCorrectTotal() {
    assertEquals(bd("502.5"), calc.calculateTotal().stripTrailingZeros());
  }

  //Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}

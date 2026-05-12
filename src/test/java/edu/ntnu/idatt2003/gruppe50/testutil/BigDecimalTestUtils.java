package edu.ntnu.idatt2003.gruppe50.testutil;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BigDecimalTestUtils {

  public static BigDecimal bd(double number) {
    return BigDecimal.valueOf(number);
  }

  public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertEquals(expected.stripTrailingZeros(), actual.stripTrailingZeros());
  }
}

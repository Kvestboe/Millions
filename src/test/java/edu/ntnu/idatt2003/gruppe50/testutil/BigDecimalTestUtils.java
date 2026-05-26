package edu.ntnu.idatt2003.gruppe50.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

public class BigDecimalTestUtils {

  public static BigDecimal bd(double number) {
    return BigDecimal.valueOf(number);
  }

  public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
    assertEquals(0, expected.compareTo(actual));
  }
}

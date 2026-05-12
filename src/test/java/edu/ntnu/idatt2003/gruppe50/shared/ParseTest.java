package edu.ntnu.idatt2003.gruppe50.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ParseTest {

  @Test
  void parsesValidInteger() {
    assertEquals(new BigDecimal("1000"), Parse.parseBigDecimal("1000"));
  }

  @Test
  void parsesValidDecimal() {
    assertEquals(new BigDecimal("3.14"), Parse.parseBigDecimal("3.14"));
  }

  @Test
  void stripsCurrencySuffix() {
    assertEquals(new BigDecimal("100"), Parse.parseBigDecimal("100kr"));
  }

  @Test
  void stripsMultipleNonNumericCharacters() {
    assertEquals(new BigDecimal("1000.50"), Parse.parseBigDecimal("$1 000.50 kr"));
  }

  @Test
  void throwsForBlankInput() {
    assertThrows(IllegalArgumentException.class, () -> Parse.parseBigDecimal(""));
  }

  @Test
  void throwsForInputWithNoDigits() {
    assertThrows(IllegalArgumentException.class, () -> Parse.parseBigDecimal("abc"));
  }

  @Test
  void throwsCorrectMessageForNoDigits() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> Parse.parseBigDecimal("abc"));
    assertEquals("Starting capital must be a valid number", ex.getMessage());
  }

  @Test
  void throwsNumberFormatExceptionForMultipleDecimalPoints() {
    assertThrows(NumberFormatException.class, () -> Parse.parseBigDecimal("1.2.3"));
  }
}

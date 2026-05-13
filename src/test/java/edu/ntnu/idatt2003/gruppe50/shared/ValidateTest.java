package edu.ntnu.idatt2003.gruppe50.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Validate")
class ValidateTest {

  @Nested
  @DisplayName("notNull()")
  class NotNull {

    @Test
    @DisplayName("Does not throw when value is non-null")
    void doesNotThrowForNonNull() {
      assertDoesNotThrow(() -> Validate.notNull("hello", "field"));
    }

    @Test
    @DisplayName("Throws when value is null")
    void throwsForNull() {
      IllegalArgumentException ex =
          assertThrows(IllegalArgumentException.class, () -> Validate.notNull(null, "myField"));
      assertEquals("myField cannot be null", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("notBlank()")
  class NotBlank {

    @Test
    @DisplayName("Does not throw for a valid string")
    void doesNotThrowForValidString() {
      assertDoesNotThrow(() -> Validate.notBlank("hello", "field"));
    }

    @Test
    @DisplayName("Throws when value is null")
    void throwsForNull() {
      IllegalArgumentException ex =
          assertThrows(IllegalArgumentException.class, () -> Validate.notBlank(null, "myField"));
      assertEquals("myField cannot be null or blank", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when value is empty string")
    void throwsForEmptyString() {
      assertThrows(IllegalArgumentException.class, () -> Validate.notBlank("", "myField"));
    }

    @Test
    @DisplayName("Throws when value is whitespace only")
    void throwsForBlankString() {
      assertThrows(IllegalArgumentException.class, () -> Validate.notBlank("   ", "myField"));
    }
  }

  @Nested
  @DisplayName("positive() [BigDecimal]")
  class PositiveBigDecimal {

    @Test
    @DisplayName("Does not throw for a positive value")
    void doesNotThrowForPositive() {
      assertDoesNotThrow(() -> Validate.positive(new BigDecimal("0.01"), "capital"));
    }

    @Test
    @DisplayName("Throws when value is null")
    void throwsForNull() {
      IllegalArgumentException ex =
          assertThrows(IllegalArgumentException.class, () -> Validate.positive(null, "capital"));
      assertEquals("capital must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when value is zero")
    void throwsForZero() {
      assertThrows(
          IllegalArgumentException.class, () -> Validate.positive(BigDecimal.ZERO, "capital"));
    }

    @Test
    @DisplayName("Throws when value is negative")
    void throwsForNegative() {
      assertThrows(
          IllegalArgumentException.class, () -> Validate.positive(new BigDecimal("-1"), "capital"));
    }
  }

  @Nested
  @DisplayName("positiveInt()")
  class PositiveInt {

    @Test
    @DisplayName("Does not throw for a positive int")
    void doesNotThrowForPositive() {
      assertDoesNotThrow(() -> Validate.positiveInt(1, "quantity"));
    }

    @Test
    @DisplayName("Throws when value is zero")
    void throwsForZero() {
      IllegalArgumentException ex =
          assertThrows(IllegalArgumentException.class, () -> Validate.positiveInt(0, "quantity"));
      assertEquals("quantity must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when value is negative")
    void throwsForNegative() {
      assertThrows(IllegalArgumentException.class, () -> Validate.positiveInt(-5, "quantity"));
    }
  }

  @Nested
  @DisplayName("notEmpty()")
  class NotEmpty {

    @Test
    @DisplayName("Does not throw for a non-empty collection")
    void doesNotThrowForNonEmpty() {
      assertDoesNotThrow(() -> Validate.notEmpty(List.of("a"), "list"));
    }

    @Test
    @DisplayName("Throws when collection is null")
    void throwsForNull() {
      IllegalArgumentException ex =
          assertThrows(IllegalArgumentException.class, () -> Validate.notEmpty(null, "list"));
      assertEquals("list cannot be null or empty", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when collection is empty")
    void throwsForEmpty() {
      assertThrows(IllegalArgumentException.class, () -> Validate.notEmpty(List.of(), "list"));
    }
  }

  @Nested
  @DisplayName("validateInput()")
  class ValidateInput {

    private final String validName = "Marius";
    private final BigDecimal validCapital = new BigDecimal("1000");
    private final File validFile = new File("stocks.csv");

    @Test
    @DisplayName("Does not throw for valid inputs")
    void doesNotThrowForValidInputs() {
      assertDoesNotThrow(() -> Validate.validateInput(validName, validCapital, validFile));
    }

    @Test
    @DisplayName("Throws when player name is blank")
    void throwsForBlankPlayerName() {
      assertThrows(
          IllegalArgumentException.class,
          () -> Validate.validateInput("  ", validCapital, validFile));
    }

    @Test
    @DisplayName("Throws when player name is null")
    void throwsForNullPlayerName() {
      assertThrows(
          IllegalArgumentException.class,
          () -> Validate.validateInput(null, validCapital, validFile));
    }

    @Test
    @DisplayName("Throws when starting capital is zero")
    void throwsForZeroCapital() {
      assertThrows(
          IllegalArgumentException.class,
          () -> Validate.validateInput(validName, BigDecimal.ZERO, validFile));
    }

    @Test
    @DisplayName("Throws when starting capital is null")
    void throwsForNullCapital() {
      assertThrows(
          IllegalArgumentException.class, () -> Validate.validateInput(validName, null, validFile));
    }

    @Test
    @DisplayName("Throws when stock file is null")
    void throwsForNullFile() {
      assertThrows(
          IllegalArgumentException.class,
          () -> Validate.validateInput(validName, validCapital, null));
    }
  }
}

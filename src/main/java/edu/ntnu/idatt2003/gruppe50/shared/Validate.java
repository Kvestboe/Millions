package edu.ntnu.idatt2003.gruppe50.shared;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Utility class providing static validation methods for common input checks.
 *
 * <p>All methods throw {@link IllegalArgumentException} if validation fails,
 * with a descriptive message identifying the invalid field by name.
 */
public class Validate {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private Validate() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Validates that the given object is not {@code null}.
   *
   * @param value the object to check
   * @param name  the name of the field, used in the exception message
   * @throws IllegalArgumentException if {@code value} is {@code null}
   */
  public static void notNull(Object value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " cannot be null");
    }
  }

  /**
   * Validates that the given string is neither {@code null} nor blank.
   *
   * @param value the string to check
   * @param name  the name of the field, used in the exception message
   * @throws IllegalArgumentException if {@code value} is {@code null} or blank
   */
  public static void notBlank(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(name + " cannot be null or blank");
    }
  }

  /**
   * Validates that the given {@link BigDecimal} is not {@code null} and strictly positive.
   *
   * @param value the value to check
   * @param name  the name of the field, used in the exception message
   * @throws IllegalArgumentException if {@code value} is {@code null} or less than or equal to zero
   */
  public static void positive(BigDecimal value, String name) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(name + " must be positive");
    }
  }

  /**
   * Validates that the given {@code int} is strictly positive.
   *
   * @param value the value to check
   * @param name  the name of the field, used in the exception message
   * @throws IllegalArgumentException if {@code value} is less than or equal to zero
   */
  public static void positiveInt(int value, String name) {
    if (value <= 0) {
      throw new IllegalArgumentException(name + " must be positive");
    }
  }

  /**
   * Validates that the given {@link Collection} is not {@code null} and not empty.
   *
   * @param value the collection to check
   * @param name  the name of the field, used in the exception message
   * @throws IllegalArgumentException if {@code value} is {@code null} or empty
   */
  public static void notEmpty(Collection<?> value, String name) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(name + " cannot be null or empty");
    }
  }

  /**
   * Validates the input parameters for starting a new game.
   *
   * @param playerName      the name of the player, must not be blank
   * @param startingCapital the starting capital, must be a positive value
   * @param stockFile       the file containing stock data, must not be null
   * @throws IllegalArgumentException if any of the parameters are invalid
   */
  public static void validateInput(String playerName, BigDecimal startingCapital, File stockFile) {
    notBlank(playerName, "Player name");
    positive(startingCapital, "Starting capital");
    notNull(stockFile, "File");
  }
}

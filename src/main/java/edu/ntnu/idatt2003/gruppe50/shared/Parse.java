package edu.ntnu.idatt2003.gruppe50.shared;

import java.math.BigDecimal;

/** Utility class for parsing user input into Java types. */
public class Parse {

  /**
   * Parses a string into a positive {@link BigDecimal}.
   *
   * <p>Common non-numeric formatting characters are stripped before parsing,
   * allowing input like {@code "100kr"} or {@code "100,-"}. Values with a
   * leading minus sign are rejected before cleanup so negative input cannot be
   * converted into a positive number.
   *
   * @param input the string to parse, must contain at least one digit and must not be negative
   * @return the parsed {@link BigDecimal} value
   * @throws IllegalArgumentException if the input is blank, contains no numeric characters,
   *     or starts with a minus sign
   */
  public static BigDecimal parseBigDecimal(String input) {
    if (input == null || input.isBlank()) {
      throw new IllegalArgumentException("Starting capital must be a valid number");
    }

    if (input.trim().startsWith("-")) {
      throw new IllegalArgumentException("Starting capital must be greater than 0 kr");
    }

    String cleaned = input.replaceAll("[^0-9.]", "");
    if (cleaned.isBlank()) {
      throw new IllegalArgumentException("Starting capital must be a valid number");
    }

    return new BigDecimal(cleaned);
  }
}

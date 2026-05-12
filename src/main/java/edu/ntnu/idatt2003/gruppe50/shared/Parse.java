package edu.ntnu.idatt2003.gruppe50.shared;

import java.math.BigDecimal;

/** Utility class for parsing user input into Java types. */
public class Parse {

  /**
   * Parses a string into a {@link BigDecimal}.
   * Non-numeric characters are stripped before parsing,
   * allowing input like "100kr" or "100,-".
   *
   * @param input the string to parse, must contain at least one digit
   * @return the parsed {@link BigDecimal} value
   * @throws IllegalArgumentException if the input contains no numeric characters
   */
  public static BigDecimal parseBigDecimal(String input) {
    String cleaned = input.replaceAll("[^0-9.]", "");
    if (cleaned.isBlank()) {
      throw new IllegalArgumentException("Starting capital must be a valid number");
    }
    return new BigDecimal(cleaned);
  }
}

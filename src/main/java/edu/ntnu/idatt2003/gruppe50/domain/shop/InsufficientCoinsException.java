package edu.ntnu.idatt2003.gruppe50.domain.shop;

/**
 * Thrown when a player attempts to spend more coins than they own.
 */
public class InsufficientCoinsException extends Exception {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public InsufficientCoinsException(String message) {
    super(message);
  }
}

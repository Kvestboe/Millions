package edu.ntnu.idatt2003.gruppe50.infrastructure.csv;

/**
 * Thrown when stock data cannot be read, parsed or written.
 */
public class InvalidStockDataException extends Exception {

  /**
   * Constructs a new exception with the specified detail message.
   *
   * @param message the detail message
   */
  public InvalidStockDataException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the underlying cause
   */
  public InvalidStockDataException(String message, Throwable cause) {
    super(message, cause);
  }
}

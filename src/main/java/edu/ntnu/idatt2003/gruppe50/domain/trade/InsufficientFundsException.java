package edu.ntnu.idatt2003.gruppe50.domain.trade;

/**
 * Exception thrown when a player does not have enough money.
 */
public class InsufficientFundsException extends RuntimeException {

  /**
   * Creates a new insufficient funds exception.
   *
   * @param message the error message
   */
  public InsufficientFundsException(String message) {
    super(message);
  }
}

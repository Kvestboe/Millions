package edu.ntnu.idatt2003.gruppe50.domain.trade;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
    super(message);
  }
}

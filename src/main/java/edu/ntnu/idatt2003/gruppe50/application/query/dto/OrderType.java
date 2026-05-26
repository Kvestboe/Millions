package edu.ntnu.idatt2003.gruppe50.application.query.dto;

/** Types of orders that can be previewed or placed from the application layer. */
public enum OrderType {
  /** Order executed immediately at the current market price. */
  MARKET,

  /** Order executed only when the stock reaches the target price. */
  TARGET_PRICE,

  /** Sell order triggered when the stock falls to the stop-loss price. */
  STOP_LOSS
}

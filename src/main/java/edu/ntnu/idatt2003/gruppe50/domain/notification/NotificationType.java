package edu.ntnu.idatt2003.gruppe50.domain.notification;

/**
 * Kinds of in-game events that produce notifications.
 *
 * <p>Used by {@link Notification} to classify why the notification was emitted,
 * which lets the UI render different icons or styling per type.
 */
public enum NotificationType {
  /**
   * A pending limit order was triggered and executed successfully.
   */
  ORDER_FILLED,
  /**
   * A pending limit order reached its expiry week without triggering.
   */
  ORDER_EXPIRED,
  /**
   * A pending limit order triggered but execution failed (e.g. insufficient funds).
   */
  ORDER_FAILED,
  /**
   * The player crossed a status threshold (Novice to Investor to Speculator).
   */
  LEVEL_UP
}

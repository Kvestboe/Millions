package edu.ntnu.idatt2003.gruppe50.domain.notification;

import java.time.LocalDateTime;

/**
 * A user-facing notification representing a game event.
 *
 * @param type the kind of event
 * @param message the human-readable message to display
 * @param week the in-game week when the event occurred
 * @param createdAt the wall-clock time the notification was created
 */
public record Notification(
    NotificationType type,
    String message,
    int week,
    LocalDateTime createdAt
) {
  public Notification(NotificationType type, String message, int week) {
    this(type, message, week, LocalDateTime.now());
  }
}
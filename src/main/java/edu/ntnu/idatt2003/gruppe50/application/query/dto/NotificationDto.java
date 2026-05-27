package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationType;

/**
 * UI-friendly representation of a notification.
 *
 * @param type    the kind of event (used by the view to pick an icon/color)
 * @param message the human-readable message to display
 * @param week    the in-game week when the event occurred
 */
public record NotificationDto(
    NotificationType type,
    String message,
    int week
) {
}
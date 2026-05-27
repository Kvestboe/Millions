package edu.ntnu.idatt2003.gruppe50.domain.notification;

import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory log of notifications for a game session.
 *
 * <p>Notifications are stored in chronological order. The log keeps the most
 * recent {@link #MAX_SIZE} entries; older entries are discarded.
 */
public class NotificationLog {

  private static final int MAX_SIZE = 100;

  private final List<Notification> notifications = new ArrayList<>();

  /**
   * Adds a notification to the log. If the log already contains
   * {@value #MAX_SIZE} entries, the oldest entry is discarded.
   *
   * @param notification the notification to add
   * @throws IllegalArgumentException if {@code notification} is null
   */
  public void add(Notification notification) {
    Validate.notNull(notification, "Notification");
    notifications.add(notification);
    if (notifications.size() > MAX_SIZE) {
      notifications.removeFirst();
    }
  }

  /**
   * Returns the most recent notifications, newest first.
   *
   * @param limit max number to return
   * @return unmodifiable list of the most recent notifications
   */
  public List<Notification> recent(int limit) {
    Validate.positiveInt(limit, "Limit");
    int from = Math.max(0, notifications.size() - limit);
    List<Notification> recent = new ArrayList<>(notifications.subList(from, notifications.size()));
    recent.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));
    return List.copyOf(recent);
  }

  /**
   * Returns the current number of notifications in the log.
   *
   * @return number of notifications, never greater than {@value #MAX_SIZE}
   */
  public int size() {
    return notifications.size();
  }
}
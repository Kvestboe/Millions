package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.NotificationDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.notification.Notification;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;
import java.util.UUID;

/**
 * Use case for fetching the most recent notifications.
 */
public final class GetNotificationsUseCase {

  private static final int DEFAULT_LIMIT = 7;

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetNotificationsUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Returns the most recent notifications, newest first.
   *
   * @param gameId the id of the game session
   * @param limit  max number to return
   * @return list of recent notifications
   * @throws GameSessionNotFoundException if the game session does not exist
   */
  public List<NotificationDto> execute(UUID gameId, int limit) {
    GameSession session = repository.findById(gameId)
        .orElseThrow(GameSessionNotFoundException::new);

    return session.getExchange().getNotifications().recent(limit).stream()
        .map(GetNotificationsUseCase::toDto)
        .toList();
  }

  /**
   * Returns the 4 most recent notifications (default limit).
   *
   * @param gameId the id of the game session
   * @return list of recent notifications
   */
  public List<NotificationDto> execute(UUID gameId) {
    return execute(gameId, DEFAULT_LIMIT);
  }

  private static NotificationDto toDto(Notification n) {
    return new NotificationDto(n.type(), n.message(), n.week());
  }
}

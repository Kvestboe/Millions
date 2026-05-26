package edu.ntnu.idatt2003.gruppe50.domain.repository;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for storing and loading game sessions.
 */
public interface GameSessionRepository {

  /**
   * Finds a game session by its id.
   *
   * @param gameId the id of the game session
   * @return the game session, or empty if it was not found
   */
  Optional<GameSession> findById(UUID gameId);

  /**
   * Saves a game session.
   *
   * @param session the game session to save
   */
  void save(GameSession session);

  /**
   * Returns all saved game sessions.
   *
   * @return all saved game sessions
   */
  List<GameSession> findAll();

  /**
   * Deletes a game session by its id.
   *
   * @param gameId the id of the game session to delete
   */
  void delete(UUID gameId);
}

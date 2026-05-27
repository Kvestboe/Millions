package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

/**
 * Thrown when reading from or writing to the game-save storage fails.
 *
 * <p>Wraps lower-level I/O or serialisation failures so callers can react to
 * persistence problems without depending on the underlying storage mechanism.
 */
public class GameSavePersistenceException extends RuntimeException {

  /**
   * Creates a new persistence exception with a message and underlying cause.
   *
   * @param message description of what failed
   * @param cause underlying I/O or serialisation exception
   */
  public GameSavePersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}

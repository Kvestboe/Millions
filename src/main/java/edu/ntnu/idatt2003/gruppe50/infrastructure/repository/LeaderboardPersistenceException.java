package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

/**
 * Thrown when reading from or writing to the leaderboard file fails.
 */
public class LeaderboardPersistenceException extends RuntimeException {

  /**
   * Creates a new leaderboard persistence exception.
   *
   * @param message description of what failed
   * @param cause underlying I/O or serialisation exception
   */
  public LeaderboardPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }
}

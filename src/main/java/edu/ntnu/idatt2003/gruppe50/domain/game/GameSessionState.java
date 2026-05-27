package edu.ntnu.idatt2003.gruppe50.domain.game;

/**
 * Represents the lifecycle state of a game session.
 */
public enum GameSessionState {
  /**
   * The game session is active and can still be played.
   */
  ACTIVE,

  /**
   * The game session is finished and can no longer be played.
   */
  FINISHED
}

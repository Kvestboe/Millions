package edu.ntnu.idatt2003.gruppe50.domain.game;

/**
 * Represents the result status of a game session.
 */
public enum GameOutcome {
  /**
   * The game is still active.
   */
  ONGOING,

  /**
   * The player has won the game.
   */
  WON,

  /**
   * The player has lost the game.
   */
  LOST
}

package edu.ntnu.idatt2003.gruppe50.application;

/**
 * Exception thrown when a requested game session cannot be found.
 */
public class GameSessionNotFoundException extends IllegalArgumentException {

  /**
   * Creates an exception with a default game-session-not-found message.
   */
  public GameSessionNotFoundException() {
    super("Game session not found");
  }
}
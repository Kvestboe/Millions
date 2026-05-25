package edu.ntnu.idatt2003.gruppe50.domain.game;

/**
 * Exception thrown when an action is attempted on a finished game session.
 */
public class GameSessionFinishedException extends IllegalStateException {

  /**
   * Creates a new exception with a default message.
   */
  public GameSessionFinishedException() {
    super("Game session is finished");
  }
}

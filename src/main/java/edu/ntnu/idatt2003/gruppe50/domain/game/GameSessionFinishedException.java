package edu.ntnu.idatt2003.gruppe50.domain.game;

public class GameSessionFinishedException extends IllegalStateException {

  public GameSessionFinishedException() {
    super("Game session is finished");
  }

}

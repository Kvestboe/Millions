package edu.ntnu.idatt2003.gruppe50.application;

public class GameSessionNotFoundException extends IllegalArgumentException {

  public GameSessionNotFoundException() {
    super("Game session not found");
  }
}

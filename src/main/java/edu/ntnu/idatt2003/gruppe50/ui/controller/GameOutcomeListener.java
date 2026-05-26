package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;

/** Listener notified when a game session reaches a non-ongoing outcome. */
public interface GameOutcomeListener {

  /**
   * Handles the final or changed game outcome.
   *
   * @param outcome the outcome reached by the game session
   */
  void onOutcome(GameOutcome outcome);
}

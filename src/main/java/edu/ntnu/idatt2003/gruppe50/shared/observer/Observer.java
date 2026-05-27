package edu.ntnu.idatt2003.gruppe50.shared.observer;

/**
 * Contract for all observer objects.
 */
public interface Observer {

  /**
   * Called when the observed subject changes state.
   */
  void update();
}

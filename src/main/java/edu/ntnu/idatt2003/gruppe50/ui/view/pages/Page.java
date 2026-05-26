package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import javafx.scene.Parent;

/**
 * Represents a single screen in the in-game UI.
 *
 * <p>Pages are managed by the navigation system and rendered inside the
 * main game layout. Each page is responsible for returning its own JavaFX
 * root node via {@link #getView()}.
 */
public interface Page {

  /**
   * Returns the JavaFX node representing this page's UI.
   *
   * @return the root node of this page's view
   */
  Parent getView();
}

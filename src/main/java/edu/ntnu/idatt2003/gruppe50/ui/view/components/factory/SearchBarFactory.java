package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.scene.control.TextField;

/**
 * Factory for search input fields.
 */
public final class SearchBarFactory {

  /**
   * Creates a search text field that runs an action whenever the text changes.
   *
   * @param prompt    prompt text shown in the field
   * @param onChanged action to run when the text changes
   * @return configured search field
   */
  public static TextField createSearchField(String prompt, Runnable onChanged) {
    TextField field = new TextField();
    field.setPromptText(prompt);
    field.textProperty().addListener((_, _, _) -> onChanged.run());
    return field;
  }
}

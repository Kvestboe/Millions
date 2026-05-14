package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.scene.control.TextField;

public final class SearchBarFactory {

  public static TextField createSearchField(String prompt, Runnable onChanged) {
    TextField field = new TextField();
    field.setPromptText(prompt);
    field.textProperty().addListener((_, _, _) -> onChanged.run());
    return field;
  }
}

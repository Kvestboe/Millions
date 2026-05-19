package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Onboarding step where the player enters their name.
 * Valid when the name field is not blank.
 */
public class NameStep implements OnboardingStep {

  private final TextField nameField = new TextField();

  /**
   * Builds and returns the name input layout.
   *
   * @return the root node of this step's view
   */
  @Override
  public Parent getView() {
    VBox container = new VBox(16);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(60, 80, 60, 80));
    container.setMaxWidth(480);
    container.getStyleClass().add("root-bg");

    Label title = new Label("Who are you?");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Every trader needs a name.");
    subtitle.getStyleClass().add("label-muted");

    Label fieldLabel = new Label("Your name:");
    fieldLabel.getStyleClass().add("field-label");

    nameField.setPromptText("E.g. Neil Armstrong...");
    nameField.getStyleClass().add("input-field");
    nameField.setMaxWidth(Double.MAX_VALUE);

    container.getChildren().addAll(title, subtitle, fieldLabel, nameField);
    return container;
  }

  /**
   * Returns true if the name field is not blank.
   *
   * @return true if valid
   */
  @Override
  public boolean isValid() {
    return !nameField.getText().isBlank();
  }

  /**
   * Sets the player name on the shared onboarding data.
   *
   * @param data the shared mutable onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {
    data.playerName = nameField.getText().trim();
  }
}
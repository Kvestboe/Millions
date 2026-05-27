package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Onboarding step where the player selects a difficulty level.
 * Only one difficulty can be selected at a time.
 * Valid when one of the three difficulty options is selected.
 */
public class DifficultyStep implements OnboardingStep {

  private Difficulty selected = null;
  private final List<VBox> cards = new ArrayList<>();
  private final Label errorLabel = new Label();

  /**
   * Builds and returns the difficulty selection layout.
   *
   * @return the root node of this step's view
   */
  @Override
  public Parent getView() {
    VBox container = new VBox(24);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(40, 80, 40, 80));
    container.getStyleClass().add("root-bg");

    Label title = new Label("Choose your mission");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("How much risk are you willing to take?");
    subtitle.getStyleClass().add("label-muted");

    VBox easy =
        createDifficultyCard("Easy", "Training mission", Difficulty.EASY, "difficulty-card-easy");
    VBox medium =
        createDifficultyCard("Medium", "Real mission", Difficulty.MEDIUM, "difficulty-card-medium");
    VBox hard =
        createDifficultyCard("Hard", "Suicide mission", Difficulty.HARD, "difficulty-card-hard");

    cards.addAll(List.of(easy, medium, hard));

    HBox cardRow = new HBox(16, easy, medium, hard);
    cardRow.setAlignment(Pos.CENTER);

    errorLabel.getStyleClass().add("error-label");
    errorLabel.setVisible(false);

    container.getChildren().addAll(title, subtitle, cardRow, errorLabel);
    return container;
  }

  /**
   * Creates a difficulty card that can be selected.
   * Stats are derived directly from the {@link Difficulty} enum values.
   *
   * @param name       display name shown as the card title
   * @param tagLine    short descriptor shown below the title
   * @param difficulty the difficulty enum value this card represents
   * @param colorClass CSS class controlling the card's risk color (border and glow)
   * @return a styled, clickable VBox card
   */
  private VBox createDifficultyCard(
      String name, String tagLine,
      Difficulty difficulty, String colorClass
  ) {
    Label nameLabel = new Label(name);
    nameLabel.getStyleClass().addAll("diff-name", "diff-name-" + difficulty.name().toLowerCase());

    Label tagLabel = new Label(tagLine);
    tagLabel.getStyleClass().add("diff-tag");

    Label statsLabel = new Label(
        "Up chance: " + (int) (difficulty.getUpChance() * 100) + "%\n"
            + "Max gain: ±" + (int) (difficulty.getMaxGain() * 100) + "%\n"
            + "Max loss: ±" + (int) (difficulty.getMaxLoss() * 100) + "%\n"
            + "Hangar cost: " + (difficulty.getHangarCostRate() * 100) + "%/week"
    );
    statsLabel.getStyleClass().add("diff-stats");

    VBox card = new VBox(8, nameLabel, tagLabel, statsLabel);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(20));
    card.setPrefWidth(200);
    card.getStyleClass().addAll("diff-card", colorClass);

    card.setOnMouseClicked(_ -> selectCard(card, difficulty));

    return card;
  }

  /**
   * Creates a single stat label with a risk color style class applied.
   *
   * @param text       the stat text to display
   * @param styleClass either {@code risk-positive} or {@code risk-negative}
   * @return a styled label
   */
  private Label statLabel(String text, String styleClass) {
    Label label = new Label(text);
    label.getStyleClass().addAll("diff-stats", styleClass);
    return label;
  }

  /**
   * Selects the clicked card and deselects all others.
   *
   * @param clicked    the card that was clicked
   * @param difficulty the difficulty associated with the clicked card
   */
  private void selectCard(VBox clicked, Difficulty difficulty) {
    selected = difficulty;
    cards.forEach(card -> card.getStyleClass().remove("diff-card-selected"));
    clicked.getStyleClass().add("diff-card-selected");
    clearError();
  }

  /**
   * Returns true if a difficulty has been selected.
   *
   * @return true if valid
   */
  @Override
  public boolean isValid() {
    if (selected == null) {
      return showError("Please choose a mission");
    }

    clearError();
    return true;
  }

  /**
   * Sets the chosen difficulty on the shared onboarding data.
   *
   * @param data the shared mutable onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {
    data.difficulty = selected;
  }

  /**
   * Shows a validation message when no difficulty has been selected.
   *
   * @param message the message to show
   * @return false so validation can return directly
   */
  private boolean showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
    return false;
  }

  /**
   * Clears the validation message when a difficulty has been selected.
   */
  private void clearError() {
    errorLabel.setText("");
    errorLabel.setVisible(false);
  }
}
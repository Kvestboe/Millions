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

    VBox easy = createDifficultyCard("Easy", "Training mission", Difficulty.EASY,
        "Up chance: 60%\nMax gain: ±6%\nMax loss: ±4%\nHangar: 0.5%/week");
    VBox medium = createDifficultyCard("Medium", "Real mission", Difficulty.MEDIUM,
        "Up chance: 50%\nMax gain: ±10%\nMax loss: ±10%\nHangar: 1.5%/week");
    VBox hard = createDifficultyCard("Hard", "Suicide mission", Difficulty.HARD,
        "Up chance: 50%\nMax gain: ±15%\nMax loss: ±20%\nHangar: 3%/week");

    cards.addAll(List.of(easy, medium, hard));

    HBox cardRow = new HBox(16, easy, medium, hard);
    cardRow.setAlignment(Pos.CENTER);

    container.getChildren().addAll(title, subtitle, cardRow);
    return container;
  }

  /**
   * Creates a difficulty card that can be selected.
   *
   * @param name       display name
   * @param tagLine    short descriptor
   * @param difficulty the difficulty enum value
   * @param stats      stats text shown on the card
   * @return a styled VBox card
   */
  private VBox createDifficultyCard(
      String name, String tagLine,
      Difficulty difficulty, String stats
  ) {
    Label nameLabel  = new Label(name);
    Label tagLabel   = new Label(tagLine);
    Label statsLabel = new Label(stats);

    nameLabel.getStyleClass().add("diff-name");
    tagLabel.getStyleClass().add("diff-tag");
    statsLabel.getStyleClass().add("diff-stats");

    VBox card = new VBox(8, nameLabel, tagLabel, statsLabel);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(20));
    card.setPrefWidth(200);
    card.getStyleClass().add("diff-card");

    card.setOnMouseClicked(_ -> selectCard(card, difficulty));

    return card;
  }

  /**
   * Selects the clicked card and deselects all others.
   *
   * @param clicked    the card that was clicked
   * @param difficulty the difficulty associated with the clicked card
   */
  private void selectCard(VBox clicked, Difficulty difficulty) {
    selected = difficulty;
    cards.forEach(card -> {
      card.getStyleClass().removeAll("diff-card", "diff-card-selected");
      card.getStyleClass().add("diff-card");
    });
    clicked.getStyleClass().removeAll("diff-card");
    clicked.getStyleClass().add("diff-card-selected");
  }

  /**
   * Returns true if a difficulty has been selected.
   *
   * @return true if valid
   */
  @Override
  public boolean isValid() {
    return selected != null;
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
}
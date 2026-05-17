package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.shared.Parse;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import java.math.BigDecimal;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Onboarding step where the player sets their starting capital.
 * Validates the amount against the chosen difficulty's max capital restriction.
 */
public class CapitalStep implements OnboardingStep {

  private final TextField capitalField = new TextField();
  private final Label errorLabel = new Label();
  private final Label gameOverLabel = new Label();
  private Difficulty difficulty;

  /**
   * Updates the difficulty — called by OnboardingFlow before showing this step,
   * so validation can take the chosen difficulty into account.
   *
   * @param difficulty the chosen difficulty level
   */
  public void setDifficulty(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * Builds and returns the capital input layout.
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

    Label title = new Label("Your starting capital");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("How much are you bringing to the market?");
    subtitle.getStyleClass().add("label-muted");

    HBox presets = buildPresets();

    Label fieldLabel = new Label("Amount (kr)");
    fieldLabel.getStyleClass().add("field-label");

    capitalField.setPromptText("10 000");
    capitalField.getStyleClass().add("input-field");
    capitalField.setMaxWidth(Double.MAX_VALUE);
    capitalField.textProperty().addListener((_, _, _) -> updateGameOverLabel());

    errorLabel.getStyleClass().add("error-label");
    errorLabel.setVisible(false);

    gameOverLabel.getStyleClass().add("label-muted");
    updateGameOverLabel();

    container.getChildren().addAll(
        title, subtitle, presets, fieldLabel,
        capitalField, gameOverLabel, errorLabel
    );
    return container;
  }

  /**
   * Builds preset capital buttons (5k, 10k, 25k, 50k).
   *
   * @return an HBox with preset buttons
   */
  private HBox buildPresets() {
    HBox presets = new HBox(8);
    presets.setAlignment(Pos.CENTER);

    List<String> amounts = difficulty == Difficulty.HARD
        ? List.of("1 000", "2 000", "3 000", "5 000")
        : difficulty == Difficulty.MEDIUM
        ? List.of("5 000", "10 000", "15 000", "25 000")
        : List.of("5 000", "10 000", "25 000", "50 000");

    amounts.forEach(amount -> {
      Button btn = new Button(amount + " kr");
      btn.getStyleClass().add("btn-secondary");
      btn.setOnAction(_ -> capitalField.setText(amount));
      presets.getChildren().add(btn);
    });

    return presets;
  }

  /**
   * Updates the game over threshold label based on current input and difficulty.
   */
  private void updateGameOverLabel() {
    try {
      BigDecimal capital = Parse.parseBigDecimal(capitalField.getText());
      if (difficulty != null) {
        BigDecimal threshold = capital.multiply(
            BigDecimal.valueOf(difficulty.getGameOverThreshold())
        );
        gameOverLabel.setText(
            "Game over if net worth drops below " + threshold.toPlainString() + " kr"
                + " (" + (int) (difficulty.getGameOverThreshold() * 100) + "%)"
        );
      }
    } catch (Exception e) {
      gameOverLabel.setText("");
    }
  }

  /**
   * Returns true if the capital is a valid number and within the
   * difficulty's max starting capital restriction.
   *
   * @return true if valid
   */
  @Override
  public boolean isValid() {
    try {
      BigDecimal capital = Parse.parseBigDecimal(capitalField.getText());
      if (capital.compareTo(BigDecimal.ZERO) <= 0) {
        return false;
      }
      if (difficulty != null) {
        return difficulty.getMaxStartingCapital()
            .map(max -> capital.compareTo(max) <= 0)
            .orElse(true);
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Sets the starting capital on the shared onboarding data.
   *
   * @param data the shared mutable onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {
    data.startingCapital = Parse.parseBigDecimal(capitalField.getText());
  }
}

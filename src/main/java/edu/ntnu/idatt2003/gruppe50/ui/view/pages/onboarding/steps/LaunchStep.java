package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import java.math.BigDecimal;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The final step in the onboarding flow.
 * Shows a summary of all player choices before launching the game.
 * Requires no input — always valid.
 */
public class LaunchStep implements OnboardingStep {

  private OnboardingFlowData flowData;

  /**
   * Updates the flow data, called by OnboardingFlow before showing this
   * step so the summary can reflect all collected choices.
   *
   * @param flowData the shared mutable onboarding data object
   */
  public void setFlowData(OnboardingFlowData flowData) {
    this.flowData = flowData;
  }

  /**
   * Builds and returns the launch summary layout.
   *
   * @return the root node of this step's view
   */
  @Override
  public Parent getView() {
    VBox container = new VBox(16);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(24, 80, 24, 80));
    container.getStyleClass().add("root-bg");

    Label title = new Label("Ready to trade?");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Review the plan before opening the market");
    subtitle.getStyleClass().add("label-muted");

    VBox summary = buildSummary();
    summary.setMaxWidth(440);

    Label tagline = new Label("Target: 1 000 000 kr. Good luck out there.");
    tagline.getStyleClass().add("label-muted");

    container.getChildren().addAll(title, subtitle, summary, tagline);
    return container;
  }

  /**
   * Builds the summary card showing all player choices.
   *
   * @return a VBox summary card
   */
  private VBox buildSummary() {
    VBox card = new VBox();
    card.getStyleClass().add("card");

    String difficultyLabel = flowData.difficulty != null
        ? flowData.difficulty.name() : "—";

    String capitalLabel = flowData.startingCapital != null
        ? flowData.startingCapital.toPlainString() + " kr" : "—";

    String fileLabel = flowData.marketName != null
        ? flowData.marketName : "—";

    String gameOverLabel = "";
    if (flowData.difficulty != null && flowData.startingCapital != null) {
      gameOverLabel = flowData.startingCapital
          .multiply(java.math.BigDecimal.valueOf(
              flowData.difficulty.getGameOverThreshold()))
          .toPlainString() + " kr";
    }

    String hangarLabel = "—";
    if (flowData.difficulty != null && flowData.startingCapital != null) {
      BigDecimal weeklyHangarCost = flowData.startingCapital
          .multiply(BigDecimal.valueOf(flowData.difficulty.getHangarCostRate()))
          .setScale(0, java.math.RoundingMode.HALF_UP);
      hangarLabel = weeklyHangarCost.toPlainString() + " kr / week";
    }

    card.getChildren().addAll(
        summaryRow("Trader", flowData.playerName != null ? flowData.playerName : "—"),
        summaryRow("Mission", difficultyLabel),
        summaryRow("Starting capital", capitalLabel),
        summaryRow("Target", "1 000 000 kr"),
        summaryRow("Market", fileLabel),
        summaryRow("Hangar cost", hangarLabel),
        summaryRow("Game over below", gameOverLabel)
    );

    return card;
  }

  /**
   * Creates a single summary row with a key and value.
   *
   * @param key   the label key
   * @param value the display value
   * @return a styled HBox row
   */
  private HBox summaryRow(String key, String value) {
    Label keyLabel = new Label(key);
    keyLabel.getStyleClass().add("label-muted");

    Label valueLabel = new Label(value);
    valueLabel.getStyleClass().add("field-label");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(keyLabel, spacer, valueLabel);
    row.setPadding(new Insets(10, 0, 10, 0));
    row.getStyleClass().add("summary-row");
    return row;
  }

  /**
   * Always returns true — no input required on the launch step.
   *
   * @return true
   */
  @Override
  public boolean isValid() {
    return true;
  }

  /**
   * No data to contribute — all data was collected in previous steps.
   *
   * @param data the shared mutable onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {
  }
}
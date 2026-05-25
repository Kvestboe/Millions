package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding;

import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ntnu.idatt2003.gruppe50.ui.view.WindowConfig;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.CapitalStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.LaunchStep;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Coordinates the onboarding flow for new game setup in Millions.
 *
 * <p>Manages navigation between onboarding steps, owns the Next/Back
 * buttons, and collects player input via {@link OnboardingFlowData}.
 * When the player completes the final step, the collected data is
 * passed to the provided {@link Consumer} as an {@link OnboardingData} record.
 */
public class OnboardingFlow {

  private final List<OnboardingStep> steps;
  private final Consumer<OnboardingData> onComplete;
  private final Runnable onBack;
  private final OnboardingFlowData flowData = new OnboardingFlowData();
  private final StackPane center = new StackPane();

  private final List<Region> dots = new ArrayList<>();

  private int currentIndex = 0;

  private Button nextBtn;
  private Button backBtn;

  /**
   * Constructs a new OnboardingFlow.
   *
   * @param steps      the ordered list of onboarding steps to navigate through
   * @param onComplete called with the collected {@link OnboardingData} when
   *                   the player completes the final step
   * @param onBack     called when the player navigates back from the first step
   */
  public OnboardingFlow(
      List<OnboardingStep> steps,
      Consumer<OnboardingData> onComplete,
      Runnable onBack
  ) {
    this.steps = steps;
    this.onComplete = onComplete;
    this.onBack = onBack;
  }

  /**
   * Builds and returns the scene for the onboarding flow.
   *
   * @return the configured JavaFX scene
   */
  public Scene getScene() {
    Parent root = buildLayout();
    showCurrentStep();
    return new Scene(root);
  }

  /**
   * Builds the main layout containing the step content area and navigation buttons.
   *
   * @return the root layout node
   */
  private Parent buildLayout() {
    HBox progressBar = buildProgressBar();

    backBtn = ButtonFactory.secondary("← Back", this::goBack);
    backBtn.setPrefWidth(160);

    nextBtn = ButtonFactory.primary("Continue →", this::goToNext);
    nextBtn.setPrefWidth(160);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    spacer.setMaxWidth(50);

    HBox navBar = new HBox(15, backBtn, spacer, nextBtn);
    navBar.setAlignment(Pos.CENTER);
    navBar.setPrefHeight(72);
    navBar.setPadding(new Insets(16, 48, 24, 48));
    navBar.getStyleClass().add("onboarding-navbar");

    ScrollPane scrollableCenter = new ScrollPane(center);
    scrollableCenter.setFitToWidth(true);
    scrollableCenter.setFitToHeight(true);
    scrollableCenter.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollableCenter.getStyleClass().add("onboarding-scroll");

    BorderPane root = new BorderPane();
    root.getStyleClass().add("root-bg");
    root.setTop(progressBar);
    root.setCenter(scrollableCenter);
    root.setBottom(navBar);

    return root;
  }

  /**
   * Attempts to advance to the next step.
   * Validates the current step first — if invalid, does nothing.
   * On the final step, collects all data and calls onComplete.
   */
  private void goToNext() {
    if (!steps.get(currentIndex).isValid()) {
      return;
    }

    steps.get(currentIndex).contribute(flowData);

    if (currentIndex == steps.size() - 1) {
      // Final step — launch the game
      onComplete.accept(flowData.toOnboardingData());
    } else {
      currentIndex++;
      showCurrentStep();
    }
  }

  /**
   * Navigates back to the previous step.
   * If already on the first step, calls onBack to return to the main menu.
   */
  private void goBack() {
    if (currentIndex == 0) {
      onBack.run();
    } else {
      currentIndex--;
      showCurrentStep();
    }
  }

  /**
   * Updates the center area to show the current step's view.
   * Also updates the Next button label on the final step.
   */
  private void showCurrentStep() {
    if (steps.get(currentIndex) instanceof CapitalStep capitalStep) {
      capitalStep.setDifficulty(flowData.difficulty);
    }

    if (steps.get(currentIndex) instanceof LaunchStep launchStep) {
      launchStep.setFlowData(flowData);
    }

    Parent stepView = steps.get(currentIndex).getView();
    stepView.maxWidth(900);
    center.getChildren().setAll(stepView);
    updateProgressBar();

    boolean isLast = currentIndex == steps.size() - 1;
    nextBtn.setText(isLast ? "Enter the market →" : "Continue →");
  }

  private HBox buildProgressBar() {
    HBox bar = new HBox(6);
    bar.setAlignment(Pos.CENTER);
    bar.setPadding(new Insets(14, 48, 14, 48));
    bar.getStyleClass().add("onboarding-progress");

    for (int i = 0; i < steps.size(); i++) {
      int stepIndex = i;

      Region dot = new Region();
      dot.getStyleClass().add("dot-inactive");
      dot.setPrefSize(10, 10);

      dot.setOnMouseClicked(_ -> {
        if (stepIndex < currentIndex) {
          currentIndex = stepIndex;
          showCurrentStep();
        }
      });

      dots.add(dot);
      bar.getChildren().add(dot);

      if (i < steps.size() - 1) {
        Region line = new Region();
        line.getStyleClass().add("progress-line");
        line.setPrefSize(10, 1.5);
        bar.getChildren().add(line);
      }
    }

    return bar;
  }

  private void updateProgressBar() {
    for (int i = 0; i < dots.size(); i++) {
      Region dot = dots.get(i);
      dot.getStyleClass().removeAll("dot-done", "dot-active", "dot-inactive");

      if (i < currentIndex) {
        dot.getStyleClass().add("dot-done");
        dot.setPrefWidth(10);
      } else if (i == currentIndex) {
        dot.getStyleClass().add("dot-active");
        dot.setPrefWidth(20);
      } else {
        dot.getStyleClass().add("dot-inactive");
        dot.setPrefWidth(18);
      }
    }
  }
}
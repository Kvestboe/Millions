package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The first step in the onboarding flow.
 * Displays the game's story and sets the scene for the player's mission.
 * Requires no input — always valid.
 */
public class StoryStep implements OnboardingStep {

  /**
   * Builds and returns the story screen layout.
   *
   * @return the scroll pane node of this step's view
   */
  @Override
  public Parent getView() {
    VBox container = new VBox(20);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(24, 80, 24, 80));
    container.getStyleClass().add("root-bg");

    Label icon = new Label("🚀");
    icon.getStyleClass().add("story-icon");

    Label title = new Label("MILLIONS");
    title.getStyleClass().add("game-title");

    Label story = new Label(
        "You've always dreamed of reaching the Moon. "
            + "NASA said you weren't good enough. "
            + "So you built your own rocket.\n\n"
            + "Years of work later, it's almost ready — "
            + "the final components, the launch clearance, the fuel. "
            + "It all comes down to 1 000 000 kr.\n\n"
            + "The rocket doesn't wait for free. "
            + "Every week it sits in the hangar, it costs you money.\n\n"
            + "Getting that money is on you. "
            + "Luckily, you know your way around the stock market. "
            + "You've done it before. You can do it again."
    );
    story.setWrapText(true);
    story.setMaxWidth(700);
    story.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    story.getStyleClass().add("story-text");

    Label bottomStory = new Label("The market opens Monday.");
    bottomStory.getStyleClass().add("story-italic");

    container.getChildren().addAll(icon, title, story, bottomStory);

    return container;
  }


  /**
   * Always returns true — no input required on the story step.
   *
   * @return true
   */
  @Override
  public boolean isValid() {
    return true;
  }

  /**
   * No data to contribute — the story step collects no player input.
   *
   * @param data the shared onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {}
}
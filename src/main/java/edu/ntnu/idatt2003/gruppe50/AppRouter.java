package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlow;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.CapitalStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.DifficultyStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.LaunchStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.MarketStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.NameStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.StoryStep;
import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AppRouter {

  private final Stage stage;
  private final AppModule module;
  private final ThemeManager themeManager;

  public AppRouter(Stage stage, AppModule module, ThemeManager themeManager) {
    this.stage = stage;
    this.module = module;
    this.themeManager = themeManager;
  }

  public void showMainMenu() {
    show(new MainMenuView(this::showNewGame, Platform::exit).getScene());
  }

  private void showNewGame() {
    List<OnboardingStep> steps = List.of(
        new StoryStep(),
        new NameStep(),
        new DifficultyStep(),
        new CapitalStep(),
        new MarketStep(stage),
        new LaunchStep()
    );
    show(new OnboardingFlow(steps, this::startGameFromOnboarding, this::showMainMenu).getScene());
  }

  private void startGameFromOnboarding(OnboardingData data) {
    UUID gameId = new NewGameController(module.startGameSession).onStartGame(
        data.playerName(),
        data.startingCapital().toString(),
        data.stockFile()
    );
    switchToGame(gameId);
  }

  private void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    show(new GameViewCoordinator(module.gameBundle(gameId)).getScene());
  }

  private void show(Scene scene) {
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
  }
}

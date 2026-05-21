package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.GameSaveDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;
import edu.ntnu.idatt2003.gruppe50.ui.view.SoundManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.WindowConfig;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.LeaderboardView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;
import edu.ntnu.idatt2003.gruppe50.ui.controller.LoadGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.LoadGameView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.SettingsView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlow;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.CapitalStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.DifficultyStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.LaunchStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.MarketStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.NameStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.StoryStep;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public final class AppRouter {

  private final Stage stage;
  private final AppModule module;
  private final ThemeManager themeManager;
  private boolean isFullscreen = false;
  private final SoundManager soundManager;

  public AppRouter(Stage stage, AppModule module, ThemeManager themeManager) {
    this.stage = stage;
    this.module = module;
    this.themeManager = themeManager;
    this.soundManager = module.soundManager;
    this.soundManager.playMusic();

    stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> isFullscreen = newVal);
  }

  public void showMainMenu() {
    MainMenuView menu = new MainMenuView(this::showNewGame, this::showSettings, Platform::exit);
    menu.setOnLoadGame(this::showLoadGame);
    menu.setOnLeaderboard(this::showLeaderboard);
    menu.setOnContinueGame(this::showContinueGame);
    show(new Scene(menu));
  }

  private void showLoadGame() {
    LoadGameController controller = new LoadGameController(
        module.getAllSaves,
        module.loadGameSession,
        module.deleteSave,
        this::switchToGame
    );
    LoadGameView view = new LoadGameView(controller, this::showMainMenu);
    show(new Scene(view));
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
        data.stockFile(),
        data.difficulty()
    );
    switchToGame(gameId);
  }

  private void showContinueGame() {
    module.getAllSaves.execute().stream()
        .max(Comparator.comparing(SaveSummaryDto::lastPlayed))
        .ifPresent(s -> switchToGame(s.gameId()));
  }

  public void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));

    GameSessionControllerBundle bundle = module.gameBundle(gameId);
    themeManager.setTheme(bundle.session().getPlayer().getActiveTheme());

    show(new GameViewCoordinator(
        bundle,
        this::showMainMenu,
        this::showNewGame,
        themeName -> {
          themeManager.setTheme(themeName);
          themeManager.apply(stage.getScene());
        },
        module.leaderboard,
        module.leaderboardFile
    ).getScene());
  }

  private void showLeaderboard() {
    LeaderboardView view = new LeaderboardView(module.leaderboard, this::showMainMenu);
    show(new Scene(view));
  }

  private void showSettings() {
    SettingsView settings = new SettingsView(
        this::showMainMenu,
        fullscreen -> {
          isFullscreen = fullscreen;
          stage.setFullScreen(fullscreen);
        },
        stage.isFullScreen(),
        soundManager
    );
    show(new Scene(settings));
  }

  private void show(Scene scene) {

    scene.addEventFilter(ActionEvent.ACTION,
        e -> {
      if (e.getTarget() instanceof Button) {
        soundManager.playClick();
      }
        });

    boolean wasFullscreen = isFullscreen;
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.setFullScreen(wasFullscreen);
    stage.show();
  }
}

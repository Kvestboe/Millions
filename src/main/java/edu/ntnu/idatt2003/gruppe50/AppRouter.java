package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.csv.InvalidStockDataException;
import edu.ntnu.idatt2003.gruppe50.ui.controller.LoadGameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;
import edu.ntnu.idatt2003.gruppe50.ui.view.SoundManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.LeaderboardView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.LoadGameView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.SettingsView;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Owns the JavaFX {@link Stage} and switches between the application's pages.
 *
 * <p>Acts as the single navigation entry point: each "show X" method builds the
 * relevant view (and any controllers it needs from {@link AppModule}), wraps it
 * in a {@link Scene} and shows it on the stage. Also coordinates global UI
 * concerns such as fullscreen state, theme switching and background music.
 */
public final class AppRouter {

  private static final Logger LOG = Logger.getLogger(AppRouter.class.getName());

  private final Stage stage;
  private final AppModule module;
  private final ThemeManager themeManager;
  private boolean isFullscreen = false;
  private final SoundManager soundManager;

  /**
   * Creates the router and starts background music.
   *
   * @param stage        the JavaFX primary stage to manage
   * @param module       application-wide module providing use cases and managers
   * @param themeManager manager used to apply visual themes to scenes
   */
  public AppRouter(Stage stage, AppModule module, ThemeManager themeManager) {
    this.stage = stage;
    this.module = module;
    this.themeManager = themeManager;
    this.soundManager = module.soundManager;
    this.soundManager.playMusic();

    stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> isFullscreen = newVal);
  }

  /**
   * Builds and shows the main menu, including a shortcut to the most recent
   * unfinished save if one exists.
   */
  public void showMainMenu() {
    SaveSummaryDto latestSave = module.getAllSaves.execute().stream()
        .filter(s -> !s.isFinished())
        .findFirst()
        .orElse(null);

    MainMenuView menu = new MainMenuView(
        latestSave,
        this::showNewGame,
        () -> showSettings(this::showMainMenu),
        Platform::exit
    );
    menu.setOnLeaderboard(() -> showLeaderboard(this::showMainMenu));
    menu.setOnLoadGame(this::showLoadGame);
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
    try {
      UUID gameId = new NewGameController(module.startGameSession, module.stockDataSource)
          .onStartGame(
              data.playerName(),
              data.startingCapital().toString(),
              data.stockFile(),
              data.difficulty()
          );
      switchToGame(gameId);
    } catch (InvalidStockDataException e) {
      showStockFileError(e);
    }
  }

  private void showStockFileError(InvalidStockDataException cause) {
    LOG.log(Level.WARNING, "Could not load stock file", cause);
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Could not load stock file");
    alert.setHeaderText("The selected stock file could not be loaded.");
    alert.setContentText(cause.getMessage());
    alert.showAndWait();
  }

  private void showContinueGame() {
    module.getAllSaves.execute().stream()
        .filter(s -> !s.isFinished())
        .findFirst()
        .ifPresent(s -> switchToGame(s.gameId()));
  }

  /**
   * Loads the given game session and shows the in-game view for it.
   *
   * @param gameId id of the saved game session to open
   */
  public void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSessionControllerBundle bundle = module.gameBundle(gameId);
    themeManager.setTheme(bundle.session().getPlayer().getActiveTheme());

    Scene[] gameScene = new Scene[1];   // holder bryter sirkelen
    GameViewCoordinator coordinator = new GameViewCoordinator(
        bundle,
        this::showMainMenu,
        this::showNewGame,
        () -> showSettings(() -> show(gameScene[0])),
        () -> showLeaderboard(() -> show(gameScene[0])),
        themeName -> {
          themeManager.setTheme(themeName);
          themeManager.apply(stage.getScene());
        },
        module.leaderboard,
        module.leaderboardFile);
    gameScene[0] = coordinator.getScene();
    show(gameScene[0]);
  }


  private void showSettings(Runnable onBack) {
    SettingsView settings = new SettingsView(
        onBack,
        fullscreen -> {
          isFullscreen = fullscreen;
          stage.setFullScreen(fullscreen);
        },
        stage.isFullScreen(),
        soundManager);
    show(new Scene(settings));
  }

  private void showLeaderboard(Runnable onBack) {
    LeaderboardView view = new LeaderboardView(module.leaderboard, onBack);
    show(new Scene(view));
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

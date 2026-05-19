package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class App extends Application {

  private final AppModule module = new AppModule();

  @Override
  public void start(Stage stage) {
    stage.getIcons().add(new Image(
        getClass().getResourceAsStream("/icons/app-icon.png")
    ));


    showMainMenu();
  }

  public void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSessionControllerBundle bundle = module.gameBundle(gameId);
    Scene scene = new GameViewCoordinator(
        bundle,
        this::showMainMenu,
        this::showNewGame,
        themeName -> {
          themeManager.setTheme(themeName);
          themeManager.apply(stage.getScene());
        }
    ).getScene();
    themeManager.apply(scene);
    stage.setFullScreen(isFullscreen);
    stage.setScene(scene);
    stage.show();
  }

  private void showMainMenu() {
    themeManager.reset();

    MainMenuView menu = new MainMenuView(
        this::showNewGame,
        this::showSettings,
        Platform::exit
    );
    Scene scene = new Scene(menu, 900, 600);
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
    Platform.runLater(() -> stage.setFullScreen(stage.isFullScreen()));
  }

  private void showNewGame() {
    themeManager.reset();

    CapitalStep capitalStep = new CapitalStep();

    List<OnboardingStep> steps = List.of(
        new StoryStep(),
        new NameStep(),
        new DifficultyStep(),
        capitalStep,
        new MarketStep(stage),
        new LaunchStep()
    );

    OnboardingFlow flow = new OnboardingFlow(
        steps,
        this::startGameFromOnboarding,
        this::showMainMenu
    );

    Scene scene = flow.getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.setFullScreen(isFullscreen);
  }

  private void startGameFromOnboarding(OnboardingData data) {
    List<Stock> stocks = CSVFileHandler.readLines(data.stockFile().toPath());
    Player player = new Player(data.playerName(), data.startingCapital());
    Exchange exchange = new Exchange("Stock exchange", stocks, new TransactionFactory());

    UUID gameId = module.startGameSession.execute(
        new StartGameSessionUseCase.Request(player, exchange, data.difficulty())
    ).gameId();

    switchToGame(gameId);
  }

  private void showSettings() {
    SettingsView settings = new SettingsView(
        this::showMainMenu,
        fullscreen -> {
          isFullscreen = fullscreen;
          stage.setFullScreen(fullscreen);
        }
    );
    Scene scene = new Scene(settings, 900, 600);
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.setFullScreen(isFullscreen);
    new AppRouter(stage, module, new ThemeManager()).showMainMenu();
  }
}

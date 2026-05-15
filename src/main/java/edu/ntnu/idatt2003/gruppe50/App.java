package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.NewGameView;
import java.util.UUID;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public final class App extends Application {

  private final AppModule module = new AppModule();
  private Stage stage;
  private ThemeManager themeManager;


  @Override
  public void start(Stage stage) {
    this.stage = stage;
    this.themeManager = new ThemeManager();
    showMainMenu();
  }

  public void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSessionControllerBundle bundle = module.gameBundle(gameId);
    Scene scene = new GameViewCoordinator(bundle).getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
  }

  private void showMainMenu() {
    MainMenuView menu = new MainMenuView(
        this::showNewGame,
        Platform::exit
    );
    Scene scene = menu.getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
  }

  private void showNewGame() {
    NewGameController controller = new NewGameController(module.startGameSession, this::switchToGame);
    NewGameView newGame = new NewGameView(stage, controller, this::showMainMenu);
    Scene scene = newGame.getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
  }
}

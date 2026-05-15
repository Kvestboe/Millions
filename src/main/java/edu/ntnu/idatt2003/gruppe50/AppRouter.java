package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.NewGameView;
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
    NewGameController controller = new NewGameController(module.startGameSession);
    show(new NewGameView(stage, controller, this::showMainMenu, this::switchToGame).getScene());
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

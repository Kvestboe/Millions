package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.WindowConfig;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX application entry point.
 *
 * <p>Builds the {@link AppModule} composition root, sets up the primary
 * {@link Stage} (window icon and default size), and hands control over to
 * {@link AppRouter} which shows the main menu.
 */
public final class App extends Application {

  private final AppModule module = new AppModule();

  @Override
  public void start(Stage stage) {
    stage.getIcons().add(new Image(
        getClass().getResourceAsStream("/icons/app-icon.png")
    ));
    stage.setWidth(WindowConfig.WIDTH);
    stage.setHeight(WindowConfig.HEIGHT);
    new AppRouter(stage, module, new ThemeManager()).showMainMenu();
  }
}
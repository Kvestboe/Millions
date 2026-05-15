package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {

  private final AppModule module = new AppModule();

  @Override
  public void start(Stage stage) {
    new AppRouter(stage, module, new ThemeManager()).showMainMenu();
  }
}

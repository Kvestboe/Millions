package edu.ntnu.idatt2003.gruppe50.ui.view.pages;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenuView{

  private final Runnable onNewGame;
  private final Runnable onQuit;

  public MainMenuView(Runnable onNewGame, Runnable onQuit) {
    this.onNewGame = onNewGame;
    this.onQuit = onQuit;
  }

  /**
   * Builds and returns the scene for the new game screen.
   *
   * @return the configured JavaFX scene
   */
  public Scene getScene() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("root-bg");

    StackPane center = new StackPane(createCardBox());
    root.setCenter(center);

    Scene scene = new Scene(root, 1280, 900);
    scene.getStylesheets().add(
        getClass().getResource("/css/newGame.css").toExternalForm()
    );
    return scene;
  }

  private VBox createCardBox() {
    VBox cardBox = new VBox();
    cardBox.getChildren().addAll(createHeader(), createButtonBox());
    return cardBox;
  }

  private VBox createHeader() {
    VBox header = new VBox();

    Label topTitle = new Label("IDATT2003");
    Label title = new Label("Millions");
    Label subTitle = new Label("A stock market game");

    header.getChildren().addAll(topTitle, title, subTitle);
    return header;
  }

  private VBox createButtonBox() {
    VBox buttonBox = new VBox(10);

    Button continueBtn = new Button("Continue");

    Button newGame = new Button("New Game");
    newGame.setOnAction(e -> onNewGame.run());

    Button loadGame = new Button("Load Game");

    Button settings = new Button("Settings");

    Button quit = new Button("Quit");
    quit.setOnAction(e -> onQuit.run());


    buttonBox.getChildren().addAll(continueBtn, newGame, loadGame, settings, quit);
    return buttonBox;
  }
}

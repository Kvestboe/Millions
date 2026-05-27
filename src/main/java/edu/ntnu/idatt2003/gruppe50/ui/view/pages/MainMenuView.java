package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Main menu shown when the application starts.
 *
 * <p>Displays the game title, a "Continue game" shortcut when a previous
 * save exists, and buttons for starting a new game, loading a game,
 * opening the leaderboard, settings, or quitting. The background features
 * a decorative line chart.
 */
public class MainMenuView extends StackPane {

  private final SaveSummaryDto latestSave;
  private final Runnable onNewGame;
  private final Runnable onQuit;
  private final Runnable onSettings;
  private Runnable onLoadGame;
  private Runnable onLeaderboard;
  private Runnable onContinueGame;

  /**
   * Constructs the main menu.
   *
   * @param latestSave the most recent save shown on the continue button, or null if none exists
   * @param onNewGame  action triggered when the player clicks "New game"
   * @param onSettings action triggered when the player clicks "Settings"
   * @param onQuit     action triggered when the player clicks "Quit"
   */
  public MainMenuView(SaveSummaryDto latestSave, Runnable onNewGame, Runnable onSettings,
                      Runnable onQuit) {
    this.latestSave = latestSave;
    this.onNewGame = onNewGame;
    this.onSettings = onSettings;
    this.onQuit = onQuit;
    build();
  }

  /**
   * Registers the action triggered when the player clicks "Load game".
   *
   * @param onLoadGame action to run when "Load game" is clicked
   */
  public void setOnLoadGame(Runnable onLoadGame) {
    this.onLoadGame = onLoadGame;
  }

  /**
   * Registers the action triggered when the player clicks "Leaderboard".
   *
   * @param onLeaderboard action to run when "Leaderboard" is clicked
   */
  public void setOnLeaderboard(Runnable onLeaderboard) {
    this.onLeaderboard = onLeaderboard;
  }

  /**
   * Registers the action triggered when the player clicks "Continue game".
   *
   * @param onContinueGame action to run when "Continue game" is clicked
   */
  public void setOnContinueGame(Runnable onContinueGame) {
    this.onContinueGame = onContinueGame;
  }

  private void build() {
    Canvas chart = buildChart();
    VBox content = buildContent();

    getStyleClass().add("main-menu");
    StackPane.setAlignment(chart, Pos.BOTTOM_CENTER);
    StackPane.setAlignment(content, Pos.CENTER);
    getChildren().addAll(chart, content);
  }

  /**
   * Creates the decorative chart canvas anchored to the bottom of the menu.
   *
   * @return a canvas that renders the background chart
   */
  private Canvas buildChart() {
    Canvas canvas = new Canvas();
    canvas.setHeight(100);
    canvas.widthProperty().bind(widthProperty());

    canvas.widthProperty().addListener((obs, oldVal, newVal) -> {
      drawChart(canvas.getGraphicsContext2D(), newVal.doubleValue());
    });

    return canvas;
  }

  /**
   * Draws the decorative line chart along the bottom of the menu.
   *
   * @param gc    the graphics context to draw on
   * @param width the current canvas width
   */
  private void drawChart(GraphicsContext gc, double width) {
    gc.clearRect(0, 0, width, 100);

    double[] xpoints = {
        0, 80, 160, 250, 340, 420, 500, 580, 640, 720, 800, 900, 1000, 1100,
        1200, 1350, 1500, 1650, 1800, width
    };
    double[] ypoints = {
        110, 88, 95, 65, 70, 42, 48, 22, 28, 18, 24, 12, 20, 8, 15, 5, 18,
        8, 12, 5
    };

    double[] xfill = {
        0, 80, 160, 250, 340, 420, 500, 580, 640, 720, 800, 900, 1000, 1100,
        1200, 1350, 1500, 1650, 1800, width, width, 0
    };
    double[] yfill = {
        110, 88, 95, 65, 70, 42, 48, 22, 28, 18, 24, 12, 20, 8, 15, 5, 18,
        8, 12, 5, 100, 100
    };

    gc.setFill(Color.web("#FFD166", 0.08));
    gc.fillPolygon(xfill, yfill, xfill.length);

    gc.setStroke(Color.web("#FFD166", 0.3));
    gc.setLineWidth(1.5);
    gc.strokePolyline(xpoints, ypoints, xpoints.length);
  }

  private VBox buildContent() {
    Separator separator = new Separator();
    separator.getStyleClass().add("menu-separator");
    separator.setMaxWidth(340);

    Label versionLabel = new Label("v2.0.0");
    versionLabel.getStyleClass().add("version-label");

    VBox content = new VBox(14);
    content.getChildren().addAll(buildHeader(),
        buildPrimaryButton(),
        buildSecondaryButtons(), separator,
        buildSystemButtons(), versionLabel
    );
    content.setAlignment(Pos.CENTER);
    content.setPrefWidth(340);

    return content;
  }

  private VBox buildHeader() {
    Label topLabel = new Label("IDATT2003");
    topLabel.getStyleClass().add("menu-top-label");

    Label title = new Label("MILLIONS");
    title.getStyleClass().add("header-title");

    Label subtitle = new Label("A stock market game");
    subtitle.getStyleClass().add("menu-subtitle");

    VBox header = new VBox();
    header.getChildren().addAll(topLabel, title, subtitle);
    header.setAlignment(Pos.CENTER);

    return header;
  }

  /**
   * Builds the prominent "Continue game" button at the top of the menu.
   *
   * <p>When a save exists, the subtitle shows the player name and current
   * week. When no save exists, the subtitle reads "No saved game" and the
   * button is disabled.
   *
   * @return the configured continue button
   */
  private Button buildPrimaryButton() {
    Label icon = new Label("▶");

    Label text = new Label("Continue game");

    Label subtitle = new Label(
        latestSave == null
            ? "No saved game"
            : latestSave.playerName() + " - Week " + latestSave.week()
    );
    subtitle.getStyleClass().add("main-menu-button-subtitle");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox content = new HBox(8, icon, text, spacer, subtitle);
    content.setAlignment(Pos.CENTER_LEFT);

    Button continueBtn = ButtonFactory.styled("", "btn-accent", () -> {
      if (onContinueGame != null) {
        onContinueGame.run();
      }
    });
    continueBtn.setGraphic(content);
    continueBtn.setMaxWidth(340);
    continueBtn.setDisable(latestSave == null);

    return continueBtn;
  }

  private HBox buildSecondaryButtons() {
    Button newGameBtn = ButtonFactory.iconButton("✚", "New Game", onNewGame);
    Button loadGameBtn = ButtonFactory.iconButton("⬆", "Load Game", () -> {
      if (onLoadGame != null) {
        onLoadGame.run();
      }
    });
    Button leaderboardBtn = ButtonFactory.iconButton("★", "Leaderboard", () -> {
      if (onLeaderboard != null) {
        onLeaderboard.run();
      }
    });
    leaderboardBtn.setOnAction(e -> {
      if (onLeaderboard != null) {
        onLeaderboard.run();
      }
    });

    HBox.setHgrow(newGameBtn, Priority.ALWAYS);
    HBox.setHgrow(loadGameBtn, Priority.ALWAYS);
    HBox.setHgrow(leaderboardBtn, Priority.ALWAYS);

    HBox secondaryButtonsBox = new HBox(10);
    secondaryButtonsBox.getChildren().addAll(newGameBtn, loadGameBtn, leaderboardBtn);
    secondaryButtonsBox.setMaxWidth(340);

    return secondaryButtonsBox;
  }

  private HBox buildSystemButtons() {
    Button settings = ButtonFactory.styled("⛭ Settings", "system-button", onSettings);
    Button quit = ButtonFactory.styled("✕ Quit", "system-button-danger", onQuit);

    HBox.setHgrow(settings, Priority.ALWAYS);
    HBox.setHgrow(quit, Priority.ALWAYS);
    settings.setMaxWidth(Double.MAX_VALUE);
    quit.setMaxWidth(Double.MAX_VALUE);

    HBox systemBox = new HBox(10);
    systemBox.getChildren().addAll(settings, quit);
    systemBox.setMaxWidth(340);

    return systemBox;
  }
}
